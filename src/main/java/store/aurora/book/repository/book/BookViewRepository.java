package store.aurora.book.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.BookView;

public interface BookViewRepository extends JpaRepository<BookView, Long> {
    long countByBookId(Long bookId);  // bookId에 해당하는 BookView의 개수 반환
}

