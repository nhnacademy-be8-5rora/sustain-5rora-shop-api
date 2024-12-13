package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, String> {
    boolean existsByCategoryId(Long categoryId);
}

