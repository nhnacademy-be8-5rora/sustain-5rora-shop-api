package store.aurora.book.service.category.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        // 부모-자식 관계 끊기
        if (category.getParent() != null) {
            category.getParent().getChildren().remove(category); // 부모의 자식 목록에서 제거
        }

        // 자식 카테고리의 부모 관계 제거
        category.getChildren().forEach(child -> child.setParent(null));

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategoryResponseDTO> getRootCategories(Pageable pageable) {
        return categoryRepository.findByParentIsNull(pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategoryResponseDTO> getChildrenCategories(Long parentId, Pageable pageable) {
        return categoryRepository.findByParentId(parentId, pageable)
                .map(this::mapToResponseDTO);
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
    @Transactional
    public CategoryResponseDTO findById(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        CategoryResponseDTO categoryDTO = null;
        if(category.isPresent()) {
            Category categoryEntity = category.get();
            categoryDTO = new CategoryResponseDTO(
                    categoryEntity.getId(),
                    categoryEntity.getName(),
                    categoryEntity.getParent() != null ? categoryEntity.getParent().getId() : null,
                    categoryEntity.getParent() != null ? categoryEntity.getParent().getName() : null,
                    categoryEntity.getDepth(),
                    convertChildrenToResponseDTO(categoryEntity.getChildren())
            );
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

    @Override
    public List<CategoryResponseDTO> getCategories() {
        // parent가 null인 카테고리들을 가져옴
        List<Category> categories = categoryRepository.findByParentIsNull();

        // Category를 CategoryResponseDTO로 변환
        return categories.stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getParent() != null ? category.getParent().getId() : null, // parent가 없을 경우 null 처리
                        category.getParent() != null ? category.getParent().getName() : null,
                        category.getDepth(),
                        convertChildrenToResponseDTO(category.getChildren()) // 자식 카테고리들도 변환
                ))
                .collect(Collectors.toList());
    }

    // 재귀적으로 자식 카테고리들을 CategoryResponseDTO로 변환하는 메서드
    private List<CategoryResponseDTO> convertChildrenToResponseDTO(List<Category> children) {
        return children.stream()
                .map(child -> new CategoryResponseDTO(
                        child.getId(),
                        child.getName(),
                        child.getParent() != null ? child.getParent().getId() : null, // parent가 없을 경우 null 처리
                        child.getParent() != null ? child.getParent().getName() : null,
                        child.getDepth(),
                        convertChildrenToResponseDTO(child.getChildren()) // 자식 카테고리가 있을 경우 재귀적으로 변환
                ))
                .toList();
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

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDepth(category.getDepth());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        return dto;
    }

}
