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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.annotation.PutMapping;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.dto.AuthorDTO;
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
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        // User 생성
        User user1 = new User("user123", "John Doe", LocalDate.of(1990, 1, 1), "20000101", "john_doe@example.com", true);
        User user2 = new User("user1234", "John Doe2", LocalDate.of(1200, 1, 1), "20000101", "john_doe@example.com", true);

        entityManager.merge(user1);
        entityManager.merge(user2);

        // Publisher 생성
        Publisher publisher = new Publisher(1L, "Penguin Books");
        entityManager.merge(publisher);  // Publisher 객체가 이미 존재하는 경우 merge() 사용

        // Series 생성
        Series series = new Series(1L, "test name");
        entityManager.merge(series);  // Series 객체가 이미 존재하는 경우 merge() 사용

        // Category 생성
        Category category1 = new Category();
        category1.setName("Example Category");
        category1.setDepth(0);
        category1.setDisplayOrder(1);
        entityManager.merge(category1);
        Category category2 = new Category();
        category2.setName("Example Category2");
        category2.setDepth(0);
        category2.setDisplayOrder(2);
        entityManager.merge(category2);

        // Book 생성
        //todo book 양방향으로 바껴서 수정해야 함
        Book book1 = new Book();
        book1.setTitle("test title");
        book1.setRegularPrice(10000);
        book1.setSalePrice(9000);
        book1.setStock(100);
        book1.setSale(true);
        book1.setIsbn("1234567890123");
        book1.setContents("sample contents");
        book1.setExplanation("test desc");
        book1.setPackaging(false);
        book1.setPublishDate(LocalDate.of(2024, 12, 12));
        book1.setPublisher(publisher); // 기존 publisher 객체
        book1.setSeries(series); // 기존 series 객체

        // Book-Category 관계 설정
        BookCategory bookCategory1 = new BookCategory();
        bookCategory1.setBook(book1);
        bookCategory1.setCategory(category1);
        book1.addBookCategory(bookCategory1);

        BookCategory bookCategory2 = new BookCategory();
        bookCategory2.setBook(book1);
        bookCategory2.setCategory(category2);
        book1.addBookCategory(bookCategory2);

        entityManager.merge(book1);

        // Book 2 생성
        Book book2 = new Book();
        book2.setTitle("test title2");
        book2.setRegularPrice(10000);
        book2.setSalePrice(9000);
        book2.setStock(100);
        book2.setSale(true);
        book2.setIsbn("1234567890124");
        book2.setContents("sample contents2");
        book2.setExplanation("test desc2");
        book2.setPackaging(false);
        book2.setPublishDate(LocalDate.of(2024, 12, 12));
        book2.setPublisher(publisher); // 기존 publisher 객체
        book2.setSeries(series); // 기존 series 객체

// Book-Category 관계 설정
        BookCategory bookCategory3 = new BookCategory();
        bookCategory3.setBook(book2);
        bookCategory3.setCategory(category1);
        book2.addBookCategory(bookCategory3);


        entityManager.merge(book2);

        // BookCategory 생성
        entityManager.merge(new BookCategory(1L, book1, category1));
        entityManager.merge(new BookCategory(2L, book1, category2));

        // Author 생성
        Author author1 = new Author(1L, "example author");
        Author author2 = new Author(2L, "example editor");
        entityManager.merge(author1);
        entityManager.merge(author2);

        // AuthorRole 생성
        AuthorRole roleAuthor = new AuthorRole(1L, AuthorRole.Role.AUTHOR);
        AuthorRole roleEditor = new AuthorRole(2L, AuthorRole.Role.EDITOR);
        entityManager.merge(roleAuthor);
        entityManager.merge(roleEditor);

        // BookAuthors 생성
        entityManager.merge(new BookAuthor(1L, author1, roleAuthor, book1));
        entityManager.merge(new BookAuthor(2L, author2, roleEditor, book1));
        entityManager.merge(new BookAuthor(3L, author2, roleAuthor, book2));

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
        review2.setBook(book1); // 동일한 book1 객체 설정
        review2.setUser(user2); // 다른 user2 객체 설정

        entityManager.merge(review1);
        entityManager.merge(review2);


    }



    @DisplayName("책 제목을 통해 책의 세부사항을 가져오는지 확인.(책이 존재하는 경우)")
    @Test
    public void testFindBooksByTitleWithDetails() {
        // Given
        String title = "test title";
        PageRequest pageable = PageRequest.of(0, 10);  // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // Then
        assertThat(result).isNotNull();
        log.debug( "testFindBooksByTitleWithDetails 메서드 결과 값 확인 {}", result.getContent());
        assertThat(result.getContent()).isNotEmpty();  // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0);  // 결과가 하나 이상이어야 함

        // 추가적인 검증 (책 제목이 검색어와 일치하는지)
        result.getContent().forEach(book -> {
            assertThat(book.getTitle()).contains("test title");
            List<AuthorDTO> authorDTO = book.getAuthors();
            authorDTO.forEach(a -> {
                assertThat(a.getName()).isNotEmpty();
                assertThat(a.getRole()).isNotNull();
            });
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


    }

    @DisplayName("작가 이름을 통해 책의 세부사항을 가져오는지 확인. (책이 존재하는경우)")
    @Test
    public void testFindBooksByAuthorNameWithDetails() {
        // Given
        String authorName = "example author";
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);
        log.debug("testFindBooksByAuthorNameWithDetails (책 존재하는 경우 )메서드 결과 값 확인 {}",result.getContent());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty(); // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0); // 결과가 하나 이상이어야 함

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
