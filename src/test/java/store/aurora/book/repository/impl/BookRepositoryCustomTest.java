package store.aurora.book.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.entity.*;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;
import store.aurora.book.repository.BookRepository;

import store.aurora.search.dto.BookSearchEntityDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(QuerydslConfiguration.class)
@DataJpaTest
@Slf4j
public class BookRepositoryCustomTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setup() {
        // 데이터 삽입을 data.sql에서 처리하므로 setup 메서드는 비워둡니다.
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
        log.debug("testFindBooksByTitleWithDetails 메서드 결과 값 확인 {}", result.getContent());

        assertThat(result.getContent()).isNotEmpty();  // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0);  // 결과가 하나 이상이어야 함

        // 추가적인 검증 (책 제목이 검색어와 일치하는지 등)
        result.getContent().forEach(book -> {
            assertThat(book.getTitle()).contains(title);
            assertThat(book.getAuthors()).isNotEmpty();  // authors가 비어 있지 않아야 함
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
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByTitleWithDetails(title, pageable);

        // Then
        assertThat(result).isNotNull();
        log.debug("testFindBooksByTitleWithDetailsByNullOrBlankTitle 메서드 결과 값 확인 {}", result.getContent());
        assertThat(result.getContent()).isEmpty();//결과가 비어있어야함.

        title="";
        result=bookRepository.findBooksByTitleWithDetails(title, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

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
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);

        // Then
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        log.debug("testFindBooksByAuthorNameWithDetailsByNullOrBlankCategoryName 결과 값 확인: {}", result.getContent());
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함

        authorName = "";
        result = bookRepository.findBooksByAuthorNameWithDetails(authorName, pageable);
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함
    }


    @DisplayName("카테고리 이름으로 책의 세부사항을 가져오는지 확인 (카테고리가 존재하는 경우)")
    @Test
    public void testFindBooksByCategoryNameWithDetails() {
        // Given
        String categoryName = "Example Category";
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByCategoryNameWithDetails(categoryName, pageable);

        // Then
        assertThat(result).isNotNull();
        log.debug("testFindBooksByCategoryNameWithDetails 메서드 결과 값 확인: {}", result.getContent());

        assertThat(result.getContent()).isNotNull(); // 결과가 비어 있지 않아야 함
        assertThat(result.getTotalElements()).isGreaterThan(0); // 결과가 하나 이상이어야 함

        // 책의 카테고리 이름이 주어진 categoryName과 일치하는지 확인
        result.getContent().forEach(book -> {
            assertThat(book.getCategoryNameList()).isNotEmpty(); // 카테고리가 비어 있지 않아야 함
            // categoryNameList가 쉼표로 구분된 문자열일 경우 분리하여 확인
            assertThat(book.getCategoryNameList())
                    .anyMatch(category -> category.split(",") // 쉼표를 기준으로 분리
                            .length > 0 && Arrays.asList(category.split(",")).contains(categoryName)); // categoryName이 목록에 포함되어야 함
        });

    }

    @DisplayName("카테고리 이름으로 책의 세부사항을 가져오는지 확인 (카테고리가 존재하지 않는 경우)")
    @Test
    public void testFindBooksByCategoryNameWithDetailsNotExists() {
        // Given
        String categoryName = "Nonexistent Category";
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByCategoryNameWithDetails(categoryName, pageable);

        // Then
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        log.debug("testFindBooksByCategoryNameWithDetailsNotExists 결과 값 확인: {}", result.getContent());
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함
    }

    @DisplayName("카테고리 이름으로 책의 세부사항을 가져오는지 확인 (카테고리 이름이 null 또는 blank)")
    @Test
    public void testFindBooksByCategoryNameWithDetailsByNullOrBlankCategoryName() {
        // Given
        String categoryName = null;
        PageRequest pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개의 결과

        // When
        Page<BookSearchEntityDTO> result = bookRepository.findBooksByCategoryNameWithDetails(categoryName, pageable);

        // Then
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        log.debug("testFindBooksByCategoryNameWithDetailsByNullOrBlankCategoryName 결과 값 확인: {}", result.getContent());
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함

        categoryName = "";
        result = bookRepository.findBooksByCategoryNameWithDetails(categoryName, pageable);
        assertThat(result).isNotNull(); // 페이지는 null이면 안 됨
        assertThat(result.getContent()).isEmpty(); // 결과가 비어 있어야 함
    }
}
