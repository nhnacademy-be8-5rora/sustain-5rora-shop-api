package store.aurora.book.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.tag.BookTag;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
    List<BookTag> findByBook(Book book);
}

