package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.Category;
import store.aurora.book.repository.BookCategoryRepository;
import store.aurora.book.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public Category createCategory(String name, Long parentId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 비어 있을 수 없습니다.");
        }

        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 카테고리 ID입니다: " + parentId));

            if (categoryRepository.existsByNameAndParent(name, parent)) {
                throw new IllegalArgumentException("같은 부모 아래에 동일한 이름의 카테고리가 존재합니다.");
            }
        } else if (categoryRepository.existsByNameAndParentIsNull(name)) {
            throw new IllegalArgumentException("최상위 부모 간 이름은 고유해야 합니다.");
        }

        // 카테고리 생성
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        category.setDepth(parent != null ? parent.getDepth() + 1 : 1);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategoryName(Long categoryId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 비어 있을 수 없습니다.");
        }

        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

        // 중복 이름 검증
        if (category.getParent() != null) {
            // 같은 부모 아래 이름 중복 검증
            if (categoryRepository.existsByNameAndParent(newName, category.getParent())) {
                throw new IllegalArgumentException("같은 부모 아래에 동일한 이름의 카테고리가 존재할 수 없습니다.");
            }
        } else {
            // 최상위 부모 간 이름 중복 검증
            if (categoryRepository.existsByNameAndParentIsNull(newName)) {
                throw new IllegalArgumentException("최상위 부모 간에는 동일한 이름을 사용할 수 없습니다.");
            }
        }

        // 이름 변경
        category.setName(newName);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

        // 연결된 책 검증
        boolean isCategoryLinkedToBooks = bookCategoryRepository.existsByCategoryId(categoryId);
        if (isCategoryLinkedToBooks) {
            throw new IllegalArgumentException("카테고리가 책과 연결되어 있어 삭제할 수 없습니다.");
        }

        // 하위 카테고리 검증
        if (categoryRepository.existsByParent(category)) {
            throw new IllegalArgumentException("하위 카테고리가 있는 경우 삭제할 수 없습니다. 하위 카테고리를 먼저 삭제하세요.");
        }

        // 삭제
        categoryRepository.delete(category);
    }



}
