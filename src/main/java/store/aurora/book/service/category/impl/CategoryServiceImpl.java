package store.aurora.book.service.category.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
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
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new InvalidCategoryException("카테고리 이름은 비어 있을 수 없습니다.");
        }

        Category parent = null;
        if (requestDTO.getParentId() != null) {
            parent = categoryRepository.findById(requestDTO.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(requestDTO.getParentId()));
            if (categoryRepository.existsByNameAndParent(requestDTO.getName(), parent)) {
                throw new InvalidCategoryException("같은 부모 아래에 동일한 이름의 카테고리가 존재합니다.");
            }
        } else if (categoryRepository.existsByNameAndParentIsNull(requestDTO.getName())) {
            throw new InvalidCategoryException("최상위 부모 간 이름은 고유해야 합니다.");
        }

        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setParent(parent);
        category.setDepth((parent != null) ? parent.getDepth() + 1 : 0);
        Integer maxOrder = categoryRepository.findMaxDisplayOrderByParent(parent);
        category.setDisplayOrder((maxOrder != null) ? maxOrder + 1 : 0);

        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategoryName(Long categoryId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new InvalidCategoryException("카테고리 이름은 비어 있을 수 없습니다.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (category.getParent() != null) {
            if (categoryRepository.existsByNameAndParent(newName, category.getParent())) {
                throw new InvalidCategoryException("같은 부모 아래에 동일한 이름의 카테고리가 존재할 수 없습니다.");
            }
        } else {
            if (categoryRepository.existsByNameAndParentIsNull(newName)) {
                throw new InvalidCategoryException("최상위 부모 간에는 동일한 이름을 사용할 수 없습니다.");
            }
        }
        category.setName(newName);

        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        boolean isCategoryLinkedToBooks = bookCategoryRepository.existsByCategoryId(categoryId);
        if (isCategoryLinkedToBooks) {
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
