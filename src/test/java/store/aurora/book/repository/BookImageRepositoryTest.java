package store.aurora.book.repository;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.entity.Publisher;
import store.aurora.storage.entity.StorageInfo;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class BookImageRepositoryTest {

    @Autowired
    private BookImageRepository bookImageRepository;

    @Autowired
    private EntityManager entityManager;

    private Book book;

    private Publisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new Publisher();
        publisher.setName("publisher");
        entityManager.persist(publisher);

        book = new Book();
        book.setTitle("Test Book");
        book.setRegularPrice(1000);
        book.setSalePrice(800);
        book.setStock(50);
        book.setSale(true);
        book.setIsbn("testIsbn");
        book.setContents("Test Contents");
        book.setPublishDate(LocalDate.now());
        book.setPublisher(publisher);
        book.setExplanation("Test Explanation");

        entityManager.persist(book);
        entityManager.flush();
    }

    @Test
    void testFindByBook() {
        // given
        StorageInfo storageInfo = new StorageInfo("name", "url");
        entityManager.persist(storageInfo);

        BookImage bookImage1 = new BookImage();
        bookImage1.setBook(book);
        bookImage1.setFilePath("image1.jpg");
        bookImage1.setStorageInfo(storageInfo);
        entityManager.persist(bookImage1);

        BookImage bookImage2 = new BookImage();
        bookImage2.setBook(book);
        bookImage2.setFilePath("image2.jpg");
        bookImage2.setStorageInfo(storageInfo);
        entityManager.persist(bookImage2);

        entityManager.flush();

        // when
        List<BookImage> bookImages = bookImageRepository.findByBook(book);

        // then
        assertThat(bookImages).isNotEmpty();
        assertThat(bookImages).hasSize(2);
        assertThat(bookImages.get(0).getFilePath()).isEqualTo("image1.jpg");
        assertThat(bookImages.get(1).getFilePath()).isEqualTo("image2.jpg");
    }

    @Test
    void testFindByBook_noImages() {
        // given
        Book newBook = new Book(); // img 가 없는 북 생성
        newBook.setTitle("New Book");
        newBook.setRegularPrice(15000);
        newBook.setSalePrice(12000);
        newBook.setStock(30);
        newBook.setSale(true);
        newBook.setIsbn("newIsbn");
        newBook.setContents("Test Contents");
        newBook.setPublishDate(LocalDate.now());
        newBook.setPublisher(publisher);
        newBook.setExplanation("Test Explanation");
        entityManager.persist(newBook);
        entityManager.flush();

        // when
        List<BookImage> bookImages = bookImageRepository.findByBook(newBook);

        // then
        assertThat(bookImages).isEmpty();
    }
}
