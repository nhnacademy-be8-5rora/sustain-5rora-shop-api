package store.aurora.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.book.entity.BookCategory;
import store.aurora.search.dto.BookCategorySearchResponseDTO;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;

public interface SearchService {
    Page<BookSearchResponseDTO> findBooksByTitleWithDetails(String title, Pageable pageable);

    Page<BookSearchResponseDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable);

    Page<BookCategorySearchResponseDTO> findBooksByCategoryNameWithDetails(String name, Pageable pageable);
}
