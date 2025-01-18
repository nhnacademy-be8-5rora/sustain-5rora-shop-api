package store.aurora.book.repository.author;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookAuthor;

import java.util.List;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> , BookAuthorRepositoryCustom {
    List<BookAuthor> findByBook(Book book);
    boolean existsByAuthorId(Long authorId);
    boolean existsByAuthorRoleId(Long authorRoleId);

}

