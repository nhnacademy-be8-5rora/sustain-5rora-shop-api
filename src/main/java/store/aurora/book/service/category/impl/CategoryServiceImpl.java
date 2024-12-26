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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

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
    public List<CategoryDTO> findCategoryByParentId(Long categoryId) {
        List<Category> categories;

        if (categoryId == null || categoryId == 0L) {
            // categoryId가 null이거나 0인 경우 부모가 없는 카테고리 조회 (최상위 카테고리)
            categories = categoryRepository.findByParentIsNull();
        } else {
            // 주어진 categoryId로 카테고리 조회
            categories = categoryRepository.findByParentId(categoryId);
        }

        // Category 엔티티를 CategoryDTO로 변환
        return categories.stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
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
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParent() != null ? category.getParent().getName() : null,
                category.getDepth(),
                category.getDisplayOrder()
        );
    }

}
