package store.aurora.book.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.entity.*;
import store.aurora.book.repository.*;
import store.aurora.book.repository.author.AuthorRepository;
import store.aurora.book.repository.author.AuthorRoleRepository;
import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.repository.author.impl.BookAuthorRepositoryCustomImpl;
import store.aurora.book.repository.book.BookRepository;
import store.aurora.book.repository.publisher.PublisherRepository;
import store.aurora.book.repository.series.SeriesRepository;
import store.aurora.document.AuthorDocument;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BookAuthorRepositoryCustomImpl.class, QuerydslConfiguration.class})
class BookAuthorRepositoryCustomImplTest {

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorRoleRepository authorRoleRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @BeforeEach
    void setUp() {
        // Publisher 저장
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher = publisherRepository.save(publisher); // 영속화

        // Series 저장 (optional)
        Series series = new Series();
        series.setName("Sample Series");
        series = seriesRepository.save(series); // 영속화

        // Book 저장
        Book book = new Book();
        book.setTitle("Sample Book");
        book.setRegularPrice(10000);
        book.setSalePrice(9000);
        book.setStock(100);
        book.setIsbn("9781234567890");
        book.setContents("Sample contents");
        book.setExplanation("Sample explanation");
        book.setPackaging(false);
        book.setActive(true);
        book.setPublishDate(LocalDate.now());
        book.setPublisher(publisher); // 영속된 Publisher 설정
        book.setSeries(series); // 영속된 Series 설정
        book = bookRepository.save(book);

        // Author 저장
        Author author = new Author();
        author.setName("Author One");
        author = authorRepository.save(author);

        // AuthorRole 저장
        AuthorRole role = new AuthorRole();
        role.setRole("Writer");
        role = authorRoleRepository.save(role);

        // BookAuthor 저장
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        bookAuthor.setAuthorRole(role);
        bookAuthorRepository.save(bookAuthor);
    }

    @Test
    void testFindAuthorsByBookId() {
        // given
        Long bookId = 1L;

        // when
        List<AuthorDocument> authors = bookAuthorRepository.findAuthorsByBookId(bookId);

        // then
        assertThat(authors).isNotEmpty();
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("Author One");
        assertThat(authors.get(0).getRole()).isEqualTo("Writer");
    }
}
