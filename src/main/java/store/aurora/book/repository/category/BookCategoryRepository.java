package store.aurora.book.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.category.BookCategory;

import java.util.List;

public interface BookCategoryRepository extends JpaRepository<BookCategory, String> {
    boolean existsByBookIdAndCategoryId(Long bookId, Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    long countByBookId(Long bookId);

    void deleteByBookIdAndCategoryIdIn(Long bookId, List<Long> categoryIds);

    List<BookCategory> findByBookId(Long bookId);}

