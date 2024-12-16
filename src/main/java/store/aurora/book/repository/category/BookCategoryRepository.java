package store.aurora.book.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.category.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, String> {
    boolean existsByCategoryId(Long categoryId);
}

