package store.aurora.book.service.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.category.Category;
import store.aurora.book.exception.category.CategoryLinkedToBooksException;
import store.aurora.book.exception.category.CategoryNotFoundException;
import store.aurora.book.exception.category.InvalidCategoryException;
import store.aurora.book.exception.category.SubCategoryExistsException;
import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.category.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public Category createCategory(String name, Long parentId) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCategoryException("카테고리 이름은 비어 있을 수 없습니다.");
        }

        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new CategoryNotFoundException(parentId));

            if (categoryRepository.existsByNameAndParent(name, parent)) {
                throw new InvalidCategoryException("같은 부모 아래에 동일한 이름의 카테고리가 존재합니다.");
            }
        } else if (categoryRepository.existsByNameAndParentIsNull(name)) {
            throw new InvalidCategoryException("최상위 부모 간 이름은 고유해야 합니다.");
        }

        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        category.setDepth(parent != null ? parent.getDepth() + 1 : 1);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategoryName(Long categoryId, String newName) {
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

        return categoryRepository.save(category);
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



}
