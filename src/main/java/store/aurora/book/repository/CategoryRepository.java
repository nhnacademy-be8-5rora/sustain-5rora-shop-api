package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndParentId(String name, Long parentId);
}

