package store.aurora.book.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.search.dto.BookCategorySearchEntityDTO;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookImageDto;
import store.aurora.book.dto.ReviewDto;
import store.aurora.search.dto.BookSearchEntityDTO;

import java.util.List;

public interface BookRepositoryCustom {
    Page<BookSearchEntityDTO> findBooksByTitleWithDetails(String title, Pageable pageable);
    Page<BookSearchEntityDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable);
    Page<BookCategorySearchEntityDTO> findBooksByCategoryNameWithDetails(String categoryName, Pageable pageable);
    BookDetailsDto findBookDetailsByBookId(Long bookId);
    List<BookImageDto> findBookImagesByBookId(Long bookId);
    List<ReviewDto> findReviewsByBookId(Long bookId);
    List<CategoryResponseDTO> findCategoryPathByBookId(Long bookId);
}

