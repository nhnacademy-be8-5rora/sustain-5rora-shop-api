package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;

import java.time.LocalDate;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> ,BookRepositoryCustom {
    boolean existsByTitleAndPublisherAndPublishDate(String title, Publisher publisher, LocalDate publishDate);

}
