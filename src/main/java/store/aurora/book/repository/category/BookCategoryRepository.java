package store.aurora.book.repository.category;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;

import java.util.List;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    boolean existsByCategoryId(Long categoryId);

    long countByBookId(Long bookId);

    List<BookCategory> findByBookIdAndCategoryIdIn(Long bookId, List<Long> categoryIds);

    List<BookCategory> findByBookId(Long bookId);

}


