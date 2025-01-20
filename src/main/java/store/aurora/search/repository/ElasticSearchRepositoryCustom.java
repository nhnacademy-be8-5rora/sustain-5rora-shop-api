package store.aurora.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.search.dto.BookSearchResponseDTO;


public interface ElasticSearchRepositoryCustom {
    Page<BookSearchResponseDTO> searchBooksByField(String field, String keyword, Pageable pageable);
    Page<BookSearchResponseDTO> searchBooksWithWeightedFields(String keyword, Pageable pageable, String userId);
}
