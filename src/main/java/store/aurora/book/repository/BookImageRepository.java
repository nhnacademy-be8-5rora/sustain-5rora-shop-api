package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;

import java.util.List;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
    List<BookImage> findByBook(Book book);
//    boolean existsByBookAndIsThumbnailTrue(Book book);
//    void updateAllThumbnailsToFalse(Long bookId);
}

