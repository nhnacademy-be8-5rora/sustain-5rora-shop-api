package store.aurora.book.repository.category;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.entity.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndParentIsNull(String name); // 최상위 부모 간 이름 중복 검증

    boolean existsByNameAndParent(String name, Category parent);

    boolean existsByParent(Category category);

    //todo 나중에 Query 말고 다른걸로 수정해야함
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parent = :parent")
    Integer findMaxDisplayOrderByParent(@Param("parent") Category parent);

    long countByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"bookCategories.book"})
    Optional<Category> findCategoryWithBooksById(Long id);
}

