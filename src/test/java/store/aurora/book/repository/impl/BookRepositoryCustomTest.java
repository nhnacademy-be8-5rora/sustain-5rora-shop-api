package store.aurora.book.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.annotation.PutMapping;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.entity.*;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.repository.*;

import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.category.CategoryRepository;
import store.aurora.review.entity.Review;
import store.aurora.review.repository.ReviewRepository;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.user.entity.Role;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserRole;
import store.aurora.user.entity.UserStatus;
import store.aurora.user.repository.RoleRepository;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(QuerydslConfiguration.class)
@DataJpaTest
public class BookRepositoryCustomTest {



    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    @Autowired
    private AuthorRoleRepository authorRoleRepository;

    @Autowired
    private BookViewRepository bookViewRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger".getClass());

    @BeforeEach
    public void setup() {
        // User 생성
        User user1 = new User("user123", "John Doe", LocalDate.of(1990, 1, 1), "010-2000-1234", "john_doe@example.com", true);
        User user2 = new User("user1234", "John Doe2", LocalDate.of(1992, 6, 15), "010-2000-5678", "john_doe2@example.com", false);
        User user3 = new User("user567", "Jane Doe", LocalDate.of(1985, 5, 10), "010-3000-1234", "jane_doe@example.com", true);
        User user4 = new User("user678", "Sam Smith", LocalDate.of(1995, 3, 20), "010-4000-5678", "sam_smith@example.com", false);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        // Publisher 생성
        Publisher publisher1 = new Publisher(1L,"Penguin Books");
        Publisher publisher2 = new Publisher(2L,"HarperCollins");
        Publisher publisher3 = new Publisher(3L,"Macmillan");
        publisherRepository.save(publisher1);
        publisherRepository.save(publisher2);
        publisherRepository.save(publisher3);

        // Series 생성
        Series series = new Series(1L,"test name");
        Series series2 = new Series(2L,"Fiction Series");
        seriesRepository.save(series);
        seriesRepository.save(series2);

        // Category 생성
        Category category1 = new Category(1L,"Example Category", null, 0, 1,new ArrayList<>());
        Category category2 = new Category(2L,"Example Category2", null, 0, 2,new ArrayList<>());
        Category category1 = new Category(1L,"Example Category", null, 0, 1);
        Category category2 = new Category(2L,"Example Category2", null, 0, 2);
        Category category3 = new Category(3L,"Science Fiction", null, 0, 3);
        Category category4 = new Category(4L,"Fantasy", null, 0, 4);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);

        // Book 생성
        //todo book 양방향으로 바껴서 수정해야 함
        // Book 생성 (6개 책 데이터 추가)
        Book book1 = new Book(
                1L,
                "A Tale of Two Cities",
                10000,
                9000,
                100,
                true,
                "1234567890123",
                "sample contents",
                "test desc",
                false,
                LocalDate.of(2024, 12, 12),
                publisher,
                series,
                new ArrayList<>(),
                new ArrayList<>()

                publisher1,
                series
        );

        Book book2 = new Book(
                2L,
                "The Great Gatsby",
                10000,
                4000,
                100,
                true,
                "1234567890124",
                "sample contents2",
                "test desc2",
                false,
                LocalDate.of(2024, 12, 12),
                publisher,
                series,
                new ArrayList<>(),
                new ArrayList<>()

                publisher2,
                series
        );

        Book book3 = new Book(
                3L,
                "1984",
                10000,
                7500,
                100,
                true,
                "1234567890125",
                "sample contents3",
                "test desc3",
                false,
                LocalDate.of(2024, 12, 12),
                publisher3,
                series2
        );

        Book book4 = new Book(
                4L,
                "To Kill a Mockingbird",
                10000,
                8800,
                100,
                true,
                "1234567890126",
                "sample contents4",
                "test desc4",
                false,
                LocalDate.of(2024, 12, 12),
                publisher1,
                series
        );

        Book book5 = new Book(
                5L,
                "Pride and Prejudice",
                5400,
                9000,
                100,
                true,
                "1234567890127",
                "sample contents5",
                "test desc5",
                false,
                LocalDate.of(2024, 12, 12),
                publisher2,
                series2
        );

        Book book6 = new Book(
                6L,
                "The Catcher in the Rye",
                10000,
                7400,
                100,
                true,
                "1234567890128",
                "sample contents6",
                "test desc6",
                false,
                LocalDate.of(2024, 12, 12),
                publisher3,
                series
        );

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);

        // BookCategory 생성
        bookCategoryRepository.save(new BookCategory(1L,book1, category1));
        bookCategoryRepository.save(new BookCategory(2L,book1, category2));
        bookCategoryRepository.save(new BookCategory(3L,book2, category2));
        bookCategoryRepository.save(new BookCategory(4L,book3, category3));
        bookCategoryRepository.save(new BookCategory(5L,book4, category4));
        bookCategoryRepository.save(new BookCategory(6L,book5, category3));

        // Author 생성
        Author author1 = new Author(1L,"Charles Dickens");
        Author author2 = new Author(2L,"F. Scott Fitzgerald");
        Author author3 = new Author(3L,"George Orwell");
        Author author4 = new Author(4L,"Harper Lee");
        Author author5 = new Author(5L,"Jane Austen");
        Author author6 = new Author(6L,"J.D. Salinger");
        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);
        authorRepository.save(author4);
        authorRepository.save(author5);
        authorRepository.save(author6);

        // AuthorRole 생성
        AuthorRole roleAuthor = new AuthorRole(1L,AuthorRole.Role.AUTHOR);
        AuthorRole roleEditor = new AuthorRole(2L,AuthorRole.Role.EDITOR);
        authorRoleRepository.save(roleAuthor);
        authorRoleRepository.save(roleEditor);

        // BookAuthors 생성
        bookAuthorRepository.save(new BookAuthor(1L,author1, roleAuthor, book1));
        bookAuthorRepository.save(new BookAuthor(2L,author2, roleAuthor, book2));
        bookAuthorRepository.save(new BookAuthor(3L,author3, roleAuthor, book3));
        bookAuthorRepository.save(new BookAuthor(4L,author4, roleAuthor, book4));
        bookAuthorRepository.save(new BookAuthor(5L,author5, roleAuthor, book5));
        bookAuthorRepository.save(new BookAuthor(6L,author6, roleAuthor, book6));

        // Review 생성
        Review review1 = new Review();
        review1.setId(1L);
        review1.setReviewRating(4); // 1~5 사이 값 설정
        review1.setReviewContent("test contents");
        review1.setReviewCreateAt(LocalDateTime.now());
        review1.setBook(book1); // book1 객체 설정, book은 @NotNull
        review1.setUser(user1); // user1 객체 설정, user는 @NotNull

        Review review2 = new Review();
        review2.setId(2L);
        review2.setReviewRating(5); // 1~5 사이 값 설정
        review2.setReviewContent("test contents");
        review2.setReviewCreateAt(LocalDateTime.now());
        review2.setBook(book2); // book2 객체 설정
        review2.setUser(user2); // user2 객체 설정

        Review review3 = new Review();
        review3.setId(3L);
        review3.setReviewRating(3); // 1~5 사이 값 설정
        review3.setReviewContent("review content for book3");
        review3.setReviewCreateAt(LocalDateTime.now());
        review3.setBook(book3); // book3 객체 설정
        review3.setUser(user3); // user3 객체 설정

        Review review4 = new Review();
        review4.setId(4L);
        review4.setReviewRating(5); // 1~5 사이 값 설정
        review4.setReviewContent("review content for book4");
        review4.setReviewCreateAt(LocalDateTime.now());
        review4.setBook(book4); // book4 객체 설정
        review4.setUser(user4); // user4 객체 설정

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        reviewRepository.save(review4);
    }




    @DisplayName("책 제목을 통해 책의 세부사항을 가져오는지 확인.(책이 존재하는 경우)")
    @Test
    public void testFindBooksByTitleWithDetails() {
        // Given
        String title = "T";
        String orderBy = "salePrice";  // 정렬 기준을 'salePrice'로 설정
        String orderDirection = "asc";  // 오름차순으로 정렬
        PageRequest pageable = PageRequest.of(0, 10,
                orderDirection.equals("asc")
                        ? Sort.by(Sort.Order.desc(orderBy))
                        : Sort.by(Sort.Order.asc(orderBy))); // 정렬 방향 설정

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // Then
        assertThat(result).isNotNull();
       log.debug( "testFindBooksByTitleWithDetails 메서드 결과 값 확인 %s", result.getContent());

        assertThat(result.getContent()).isNotEmpty();  // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0);  // 결과가 하나 이상이어야 함

        // 추가적인 검증 (책 제목이 검색어와 일치하는지)
        result.getContent().forEach(book -> {
            assertThat(book.getTitle()).contains(title);
            assertThat(book.getAuthors()).isNotEmpty();  // authors가 비어 있지 않아야 함
            assertThat(book.getAuthors()).anyMatch(author -> author.getName() != null);  // 작가 이름이 null이 아니어야 함
            assertThat(book.getSalePrice()).isGreaterThan(0);  // 책 가격이 0보다 커야 함
            assertThat(book.getRegularPrice()).isGreaterThan(0);
            assertThat(book.getPublishDate()).isNotNull();  // 책의 발행일자가 null이 아니어야 함
        });
    }

    @DisplayName("책 제목을 통해 책의 세부사항을 가져오는지 확인.(책이 존재하지 않는 경우)")
    @Test
    public void testFindBooksByTitleWithDetailsNotExists() {
        // Given
        String title = "not exist title";
        PageRequest pageable = PageRequest.of(0, 10);  // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // Then
        assertThat(result).isNotNull();
        log.debug("testFindBooksByTitleWithDetails 메서드 결과 값 확인 {}", result.getContent());

        assertThat(result.getContent()).isEmpty();//결과가 비어있어야함.
    }

    @DisplayName("책 제목을 통해 책의 세부사항을 가져오는지 확인. (책 제목이 null 또는 blank)")
    @Test
    public void testFindBooksByTitleWithDetailsByNullOrBlankTitle() {
        // Given
        String title = null;
        PageRequest pageable = PageRequest.of(0, 10);  // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> nullTitleResult = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // Then
        assertThat(nullTitleResult).isNotNull();
        log.debug("testFindBooksByTitleWithDetailsByNullOrBlankTitle 메서드 결과 값 확인 {}", nullTitleResult.getContent());
        assertThat(nullTitleResult.getContent()).isEmpty();//결과가 비어있어야함.

        title="";
        Page<BookSearchEntityDTO> emptyTitleResult=bookRepository.findBooksByTitleWithDetails(title, pageable);
        assertThat(emptyTitleResult).isNotNull();
        assertThat(emptyTitleResult.getContent()).isEmpty();

    }

    @DisplayName("작가 이름을 통해 책의 세부사항을 가져오는지 확인. (책이 존재하는 경우, 정렬 포함)")
    @Test
    public void testFindBooksByAuthorNameWithDetails() {
        // Given
        String authorName = "Charles";
        String orderBy = "title";  // 정렬 기준을 'title'로 설정
        String orderDirection = "asc";  // 오름차순으로 정렬
        PageRequest pageable = PageRequest.of(0, 10, orderDirection.equals("desc")
                ? Sort.by(Sort.Order.desc(orderBy))
                : Sort.by(Sort.Order.asc(orderBy))); // 정렬 방향 설정

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);
        log.debug("testFindBooksByAuthorNameWithDetails (책 존재하는 경우 )메서드 결과 값 확인 {}", result.getContent());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty(); // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0); // 결과가 하나 이상이어야 함

        // 정렬 검증: 반환된 책들이 title을 기준으로 오름차순으로 정렬되어 있는지 확인
        List<BookSearchEntityDTO> books = result.getContent();
        for (int i = 1; i < books.size(); i++) {
            assertThat(books.get(i - 1).getTitle()).isLessThanOrEqualTo(books.get(i).getTitle());  // 오름차순 정렬 확인
        }

        // 추가적인 검증
        result.getContent().forEach(book -> {
            assertThat(book.getAuthors()).isNotEmpty(); // authors가 비어 있지 않아야 함
            assertThat(book.getAuthors()).anyMatch(author -> author.getName().contains(authorName)); // 작가 이름 포함 여부 검증
            assertThat(book.getTitle()).isNotBlank(); // 도서 제목이 비어 있지 않아야 함
            assertThat(book.getRegularPrice()).isGreaterThanOrEqualTo(0); // regularPrice가 음수가 아니어야 함
            assertThat(book.getSalePrice()).isGreaterThanOrEqualTo(0); // salePrice가 음수가 아니어야 함
            assertThat(book.getPublishDate()).isNotNull(); // publishDate가 null이 아니어야 함
            assertThat(book.getPublishDate()).isBeforeOrEqualTo(LocalDate.now()); // 출판일이 오늘 날짜 이전이어야 함

            // 리뷰 및 조회수 검증
            assertThat(book.getReviewCount()).isGreaterThanOrEqualTo(0); // 리뷰 개수가 음수가 아니어야 함
            assertThat(book.getViewCount()).isGreaterThanOrEqualTo(0); // 조회수가 음수가 아니어야 함

            assertThat(book.getAuthors()).allMatch(author -> {
                AuthorRole.Role role = author.getRole();
                return role != null;
            });
        });
    }


    @DisplayName("작가 이름을 통해 책의 세부사항을 가져오는지 확인. (책이 존재하지 않는경우)")
    @Test
    public void testFindBooksByAuthorNameWithDetailsNotExists() {
        // Given
        String authorName = "not exist author";
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);
        log.debug("testFindBooksByAuthorNameWithDetails (책 존재하지 않는 경우) 메서드 결과 값 확인 {}",result.getContent());

        // Then
        assertThat(result).isNotNull(); //존재하지않아도 빈 페이지를 보여주기에 결과는 null이면 안된다
        assertThat(result.getContent()).isEmpty(); // 결과가 비어있어여함

    }

    @DisplayName("작가 이름이 null 또는 blank면 빈페이지")
    @Test
    public void testFindBooksByAuthorNameWithDetailsByNullOrBlankCategoryName() {
        // Given
        String authorName = null;
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> nullNameResult = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);

        // Then
        assertThat(nullNameResult).isNotNull(); // 페이지는 null이면 안 됨
        log.debug("testFindBooksByAuthorNameWithDetailsByNullOrBlankCategoryName 결과 값 확인: {}", nullNameResult.getContent());
        assertThat(nullNameResult.getContent()).isEmpty(); // 결과가 비어 있어야 함

        authorName = "";
        Page<BookSearchEntityDTO> emptyNameResult = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);
        assertThat(emptyNameResult).isNotNull(); // 페이지는 null이면 안 됨
        assertThat(emptyNameResult.getContent()).isEmpty(); // 결과가 비어 있어야 함
    }


    @DisplayName("카테고리 Id로 책의 세부사항을 가져오는지 확인 (카테고리가 존재하는 경우)")
    @Test
    public void testFindBooksByCategoryIdWithDetails() {
        // Given
        Long categoryId = 1L;
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByCategoryWithDetails(categoryId, pageable);

        // Then
        assertThat(result).isNotNull();
        log.debug("testFindBooksByCategoryIdWithDetails 메서드 결과 값 확인: {}", result.getContent());

        assertThat(result.getContent()).isNotNull(); // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0); // 결과가 하나 이상이어야 함

        // 책의 카테고리 Id가 주어진 categoryId와 일치하는지 확인
        result.getContent().forEach(book -> {
            assertThat(book.getCategoryIdList()).isNotEmpty(); // 카테고리가 비어 있지 않아야 함
            assertThat(book.getCategoryIdList()).contains(categoryId); // 카테고리 목록에 categoryId가 포함되어 있는지 확인

            // 추가적인 검증

            // 책 제목이 비어 있지 않아야 함
            assertThat(book.getTitle()).isNotBlank();

            // 가격이 음수일 수 없음
            assertThat(book.getRegularPrice()).isGreaterThanOrEqualTo(0);
            assertThat(book.getSalePrice()).isGreaterThanOrEqualTo(0);

            // 출판일이 null이 아니어야 하며, 미래 날짜가 아니라 오늘 날짜 이전이어야 함
            assertThat(book.getPublishDate()).isNotNull();
            assertThat(book.getPublishDate()).isBeforeOrEqualTo(LocalDate.now());

            // 책의 저자 정보 검증
            assertThat(book.getAuthors()).isNotEmpty(); // 저자 목록이 비어 있지 않아야 함
            book.getAuthors().forEach(author -> {
                assertThat(author.getName()).isNotBlank(); // 작가 이름이 비어 있지 않아야 함
            });

            // 리뷰 개수와 조회수 검증 (음수일 수 없음)
            assertThat(book.getReviewCount()).isGreaterThanOrEqualTo(0);
            assertThat(book.getViewCount()).isGreaterThanOrEqualTo(0);

        });
    }


    @DisplayName("카테고리 Id로 책의 세부사항을 가져오는지 확인 (카테고리가 존재하지 않는 경우)")
    @Test
    public void testFindBooksByCategoryIdWithDetailsNotExists() {
        // Given
        Long categoryId = 13L;
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByCategoryWithDetails(categoryId, pageable);

        // Then
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        log.debug("testFindBooksByCategoryIdWithDetailsNotExists 결과 값 확인: {}", result.getContent());
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함
    }

    @DisplayName("카테고리 Id로 책의 세부사항을 가져오는지 확인 (카테고리 이름이 null 또는 blank)")
    @Test
    public void testFindBooksByCategoryIdWithDetailsByNullOrBlankCategoryName() {
        // Given
        Long categoryId = null;
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByCategoryWithDetails(categoryId, pageable);

        // Then
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        log.debug("testFindBooksByCategoryIdWithDetailsByNullOrBlankCategoryName 결과 값 확인: {}", result.getContent());
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함

    }
}
