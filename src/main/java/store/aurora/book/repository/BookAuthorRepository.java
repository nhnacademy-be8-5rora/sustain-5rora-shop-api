package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;

import java.util.List;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    List<BookAuthor> findByBook(Book book);

}

