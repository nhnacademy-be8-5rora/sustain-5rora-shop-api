package store.aurora.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;

public interface SearchService {
    Page<BookSearchResponseDTO> findBooksByTitleWithDetails(String title, Pageable pageable);

    Page<BookSearchResponseDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable);

    Page<BookSearchResponseDTO> findBooksByCategoryWithDetails(Long categoryId, Pageable pageable);
}
