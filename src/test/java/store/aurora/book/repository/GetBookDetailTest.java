package store.aurora.book.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.BookImageDto;
import store.aurora.book.dto.category.BookCategoryDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.category.CategoryRepository;
import store.aurora.cart.repository.CartItemRepository;
import store.aurora.cart.repository.CartRepository;
import store.aurora.review.entity.Review;
import store.aurora.review.repository.ReviewRepository;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(QuerydslConfiguration.class)
@DataJpaTest
public class GetBookDetailTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookImageRepository bookImageRepository;


    private Book book;
    private Publisher publisher;

    @BeforeEach
    void setUp() {
        // 1. Publisher 생성
        publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisherRepository.save(publisher);

        // 2. Book 생성
        book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setRegularPrice(10000);
        book.setSalePrice(8000);
        book.setExplanation("This is a test book.");
        book.setContents("Book contents...");
        book.setPublishDate(LocalDate.of(2022, 1, 1));
        book.setPublisher(publisher);
        bookRepository.save(book);

        //TODO storage, review 등 필요한 entity들이 아직 구현이 안되어있어서 테스트를 다 하지 못했음
    }

    @Test
    void testFindBookDetailsByBookId() {
        // When
        BookDetailsDto bookDetails = bookRepository.findBookDetailsByBookId(book.getId());

        // Then
        assertThat(bookDetails).isNotNull();
        assertThat(bookDetails.getBookId()).isEqualTo(book.getId());
        assertThat(bookDetails.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookDetails.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(bookDetails.getExplanation()).isEqualTo(book.getExplanation());
    }


    @Test
    void testFindCategoryPathByBookId() {
        // Given: 카테고리 추가
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        parentCategory.setDepth(0);
//        parentCategory.setDisplayOrder(1);
        categoryRepository.save(parentCategory);

        Category subCategory = new Category();
        subCategory.setName("Sub Category");
        subCategory.setParent(parentCategory);
        subCategory.setDepth(1);
//        subCategory.setDisplayOrder(1);
        categoryRepository.save(subCategory);

        BookCategory bookCategory = new BookCategory();
        bookCategory.setBook(book);
        bookCategory.setCategory(subCategory);
        bookCategoryRepository.save(bookCategory);

        // When
        List<BookCategoryDto> categories = bookRepository.findCategoryPathByBookId(book.getId());

        // Then
        assertThat(categories)
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(category -> {
                    assertThat(category.getName()).isEqualTo("Parent Category");
                    assertThat(category.getChildren())
                            .hasSize(1)
                            .first()
                            .satisfies(child -> assertThat(child.getName()).isEqualTo("Sub Category"));
                });
    }
}


