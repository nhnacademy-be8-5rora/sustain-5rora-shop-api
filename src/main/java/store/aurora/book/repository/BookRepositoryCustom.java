package store.aurora.book.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.search.dto.BookSearchEntityDTO;

public interface BookRepositoryCustom {
    Page<BookSearchEntityDTO> findBooksByTitleWithDetails(String title, Pageable pageable);
    Page<BookSearchEntityDTO> findBooksByAuthorNameWithDetails(String name, Pageable pageable);

}

