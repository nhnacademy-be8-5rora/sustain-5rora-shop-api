package store.aurora.book.service.category.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.exception.category.CategoryLinkedToBooksException;
import store.aurora.book.exception.category.CategoryNotFoundException;
import store.aurora.book.exception.category.InvalidCategoryException;
import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.category.CategoryRepository;
import store.aurora.book.service.category.CategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;


    @Override
    public Page<CategoryResponseDTO> getPagedCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(category -> {
            CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setDepth(category.getDepth());
            dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
            return dto;
        });
    }

    @Override
    public List<CategoryResponseDTO> getChildrenCategories(Long parentId) {
        List<Category> childCategories = categoryRepository.findByParentId(parentId);

        // 직속 하위 카테고리를 DTO로 변환
        return childCategories.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }
    @Override
    public Page<CategoryResponseDTO> getPagedChildrenCategories(Long parentId, Pageable pageable) {
        Page<Category> childCategories = categoryRepository.findByParentId(parentId, pageable);

        // 직속 하위 카테고리를 DTO로 변환
        return childCategories
                .map(this::mapToResponseDTO);

    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponseDTO> getRootCategories() {
        // 부모가 없는 루트 카테고리를 조회
        List<Category> rootCategories = categoryRepository.findByParentIsNull();

        // CategoryResponseDTO로 변환
        return rootCategories.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategoryResponseDTO> getPagedRootCategories(Pageable pageable) {
        // 부모가 없는 루트 카테고리를 조회
        Page<Category> rootCategories = categoryRepository.findByParentIsNull(pageable);

        // CategoryResponseDTO로 변환
        return rootCategories.map(this::mapToResponseDTO);
    }
    @Override
    public List<CategoryResponseDTO> getCategoryHierarchy() {
        // 루트 카테고리를 한 번의 쿼리로 가져옵니다.
        List<Category> rootCategories = categoryRepository.findAllRootCategoriesWithChildren();

        // 트리 구조 생성
        return rootCategories.stream()
                .map(this::buildCategoryHierarchy)
                .toList();
    }

    private CategoryResponseDTO buildCategoryHierarchy(Category category) {
        // 현재 카테고리를 DTO로 변환
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDepth(category.getDepth());

        // 하위 카테고리 처리 (재귀 호출)
        dto.setChildren(category.getChildren().stream()
                .map(this::buildCategoryHierarchy)
                .toList());

        return dto;
    }


    @Override
    @Transactional
    public void createCategory(CategoryRequestDTO requestDTO) {
        validateCategoryName(requestDTO.getName());
        Category parent = getParentCategory(requestDTO.getParentId());
        validateUniqueCategoryName(requestDTO.getName(), parent);

        Category category = buildCategory(requestDTO, parent);
        categoryRepository.save(category);
    }

    private Category buildCategory(CategoryRequestDTO requestDTO, Category parent) {
        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setParent(parent);

        if (parent != null) {
            parent.addChild(category); // 부모의 depth를 기반으로 계산
        } else {
            category.setDepth(0); // 루트 카테고리
        }

//        category.setDisplayOrder(calculateDisplayOrder(parent));
        return category;
    }

    @Override
    @Transactional
    public void updateCategoryName(Long categoryId, String newName) {
        validateCategoryName(newName);

        Category category = findCategoryByIdOrThrow(categoryId);

        validateUniqueCategoryName(newName, category.getParent());
        category.setName(newName);

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = findCategoryByIdOrThrow(categoryId);

        if (bookCategoryRepository.existsByCategoryId(categoryId)) {
            throw new CategoryLinkedToBooksException(categoryId);
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksByCategoryId(Long categoryId) {
        Category category = categoryRepository.findCategoryWithBooksById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return category.getBookCategories().stream()
                .map(BookCategory::getBook)
                .toList();
    }

    @Override
    public CategoryDTO findById(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        CategoryDTO categoryDTO = null;
        if(category.isPresent()) {
            categoryDTO= new CategoryDTO(category.get().getId(), category.get().getName(), convertChildrenToDTO(category.get().getChildren()));
        }
        return categoryDTO;
    }

    @Transactional
    @Override
    public List<BookCategory> createBookCategories(List<Long> categoryIds) {
        if (categoryIds.isEmpty() || categoryIds.size() > 10) {
            throw new IllegalArgumentException("카테고리는 최소 1개 이상, 최대 10개 이하만 선택할 수 있습니다.");
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        return categories.stream()
                .map(category -> {
                    BookCategory bookCategory = new BookCategory();
                    bookCategory.setCategory(category);
                    return bookCategory;
                })
                .collect(Collectors.toList());
    }

    // 자식 카테고리를 CategoryDTO로 변환하는 메서드
    private List<CategoryDTO> convertChildrenToDTO(List<Category> children) {
        return children.stream()
                .map(child -> new CategoryDTO(child.getId(), child.getName(), convertChildrenToDTO(child.getChildren())))
                .collect(Collectors.toList());
    }



    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCategoryException("카테고리 이름은 비어 있을 수 없습니다.");
        }
    }

    private Category findCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }


    private Category getParentCategory(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException(parentId));
    }

    private void validateUniqueCategoryName(String name, Category parent) {
        boolean exists;
        if (parent != null) {
            exists = categoryRepository.existsByNameAndParent(name, parent);
        } else {
            exists = categoryRepository.existsByNameAndParentIsNull(name);
        }

        if (exists) {
            throw new InvalidCategoryException("해당 이름의 카테고리가 이미 존재합니다.");
        }
    }

//    private int calculateDisplayOrder(Category parent) {
//        Integer maxOrder = categoryRepository.findMaxDisplayOrderByParent(parent);
//        return (maxOrder != null) ? maxOrder + 1 : 0;
//    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDepth(category.getDepth());
        dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        return dto;
    }

}
