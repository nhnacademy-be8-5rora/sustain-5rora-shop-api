package store.aurora.book.service.category.impl;

import lombok.RequiredArgsConstructor;
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
import store.aurora.book.exception.category.SubCategoryExistsException;
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

    @Transactional
    public void createCategory(CategoryRequestDTO requestDTO) {
        validateCategoryName(requestDTO.getName());


        Category parent = getParentCategory(requestDTO.getParentId());
        validateUniqueCategoryName(requestDTO.getName(), parent);

        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setParent(parent);
        category.setDepth((parent != null) ? parent.getDepth() + 1 : 0);
        category.setDisplayOrder(calculateDisplayOrder(parent));

        categoryRepository.save(category);
    }


    @Transactional
    public void updateCategoryName(Long categoryId, String newName) {
        validateCategoryName(newName);

        Category category = findCategoryByIdOrThrow(categoryId);

        validateUniqueCategoryName(newName, category.getParent());
        category.setName(newName);

        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = findCategoryByIdOrThrow(categoryId);

        if (bookCategoryRepository.existsByCategoryId(categoryId)) {
            throw new CategoryLinkedToBooksException(categoryId);
        }

        if (categoryRepository.existsByParent(category)) {
            throw new SubCategoryExistsException(categoryId);
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

    private int calculateDisplayOrder(Category parent) {
        Integer maxOrder = categoryRepository.findMaxDisplayOrderByParent(parent);
        return (maxOrder != null) ? maxOrder + 1 : 0;
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setDepth(category.getDepth());
        dto.setDisplayOrder(category.getDisplayOrder());

        // 자식 카테고리를 재귀적으로 매핑
        List<CategoryResponseDTO> childDTOs = category.getChildren().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        dto.setChildren(childDTOs);

        return dto;
    }

}
