package store.aurora.book.repository.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> , BookRepositoryCustom {
    boolean existsById(Long id);
    boolean existsByIsbn(String isbn13);
    boolean existsByPublisherId(Long id);
    boolean existsBySeriesId(Long id);
    Page<Book> findByActive(boolean isActive, Pageable pageable);
    List<Book> findALlByIdIn(List<Long> ids);

}
