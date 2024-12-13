package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.entity.Category;
import store.aurora.book.repository.BookCategoryRepository;
import store.aurora.book.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public Category createCategory(String name, Long parentId) {
        // 부모 카테고리 선택 여부에 따라 중복 검증
        boolean isDuplicate = categoryRepository.existsByNameAndParentId(name, parentId);
        if (isDuplicate) {
            throw new IllegalArgumentException("같은 부모 아래에 동일한 이름의 카테고리가 존재합니다.");
        }
        Category category = new Category();
        category.setName(name);

        if (parentId != null) {
            // 부모 카테고리 존재 여부 확인
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 카테고리 ID입니다: " + parentId));
            category.setParent(parent);
            category.setDepth(parent.getDepth() + 1); // 부모의 depth + 1로 설정
        } else {
            // 최상위 카테고리일 경우
            category.setDepth(1); // 최상위 카테고리는 depth 1
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategoryName(Long categoryId, String newName) {
        // 수정하려는 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

        // 중복 이름 검증
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        boolean isDuplicate = categoryRepository.existsByNameAndParentId(newName, parentId);
        if (isDuplicate) {
            throw new IllegalArgumentException("같은 부모 아래에 동일한 이름의 카테고리가 존재합니다.");
        }

        // 새로운 이름 검증
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 비어 있을 수 없습니다.");
        }

        // 이름 변경
        category.setName(newName);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));

        // 연결된 책 검증
        boolean isCategoryLinkedToBooks = bookCategoryRepository.existsByCategoryId(categoryId);
        if (isCategoryLinkedToBooks) {
            throw new IllegalArgumentException("카테고리가 책과 연결되어 있어 삭제할 수 없습니다.");
        }

        // 하위 카테고리 검증
        if (!category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("하위 카테고리가 있는 경우 삭제할 수 없습니다. 하위 카테고리를 먼저 삭제하세요.");
        }

        // 삭제
        categoryRepository.delete(category);
    }

}
