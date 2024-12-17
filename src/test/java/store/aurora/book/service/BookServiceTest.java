package store.aurora.book.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.repository.BookImageRepository;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.service.category.BookCategoryService;
import store.aurora.book.service.tag.TagService;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@ExtendWith(MockitoExtension.class) // Mock 객체를 초기화 / JUnit 5
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private PublisherService publisherService;

    @Mock
    private SeriesService seriesService;


    @Mock
    private TagService tagService;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private BookImage bookImage1;

    @BeforeEach
    void setUp() {
        // Given: Preparing mock data
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setRegularPrice(1000);
        book1.setSalePrice(800);
        book1.setStock(10);
        book1.setSale(true);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setRegularPrice(1500);
        book2.setSalePrice(1200);
        book2.setStock(5);
        book2.setSale(false);

        bookImage1 = new BookImage();
        bookImage1.setFilePath("path/to/book1/image.jpg");
        bookImage1.setBook(book1);
    }

    @Test
    void testGetBookInfo() {
        // Given: Mock repository calls
        when(bookRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(book1, book2));
        when(bookImageRepository.findByBook(book1)).thenReturn(Arrays.asList(bookImage1));
        when(bookImageRepository.findByBook(book2)).thenReturn(Arrays.asList());  // No image for book2

        // When: Calling service method
        List<BookInfoDTO> result = bookService.getBookInfo(Arrays.asList(1L, 2L));

        // Then: Verifying the results
        assertThat(result).hasSize(2);

        // Book 1 checks
        BookInfoDTO bookInfo1 = result.get(0);
        assertThat(bookInfo1.getTitle()).isEqualTo("Book 1");
        assertThat(bookInfo1.getRegularPrice()).isEqualTo(1000);
        assertThat(bookInfo1.getSalePrice()).isEqualTo(800);
        assertThat(bookInfo1.getStock()).isEqualTo(10);
        assertThat(bookInfo1.isSale()).isTrue();
        assertThat(bookInfo1.getFilePath()).isEqualTo("path/to/book1/image.jpg");

        // Book 2 checks
        BookInfoDTO bookInfo2 = result.get(1);
        assertThat(bookInfo2.getTitle()).isEqualTo("Book 2");
        assertThat(bookInfo2.getRegularPrice()).isEqualTo(1500);
        assertThat(bookInfo2.getSalePrice()).isEqualTo(1200);
        assertThat(bookInfo2.getStock()).isEqualTo(5);
        assertThat(bookInfo2.isSale()).isFalse();
        assertThat(bookInfo2.getFilePath()).isNull();
    }
}
