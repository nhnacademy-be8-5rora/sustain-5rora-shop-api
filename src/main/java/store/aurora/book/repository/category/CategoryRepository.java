package store.aurora.book.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndParentIsNull(String name); // 최상위 부모 간 이름 중복 검증

    boolean existsByNameAndParent(String name, Category parent);

    boolean existsByParent(Category category);
}

