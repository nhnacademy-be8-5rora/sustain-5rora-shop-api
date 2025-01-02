package store.aurora.book.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.dto.BookDetailsDto;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookImageDto;
import store.aurora.book.dto.PublisherDto;
import store.aurora.book.dto.ReviewDto;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.service.impl.BookServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@Transactional // todo 지우기
@Import(QuerydslConfiguration.class) // todo 지우기 고려
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    //TODO book entity 바껴서 [ERROR] 수정해야 함
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this); // 초기화
//    }

    @Test
    void testGetBookDetails_Success() {
        // Given

        List<BookImageDto> bookImageDtoList = new ArrayList<>();
        bookImageDtoList.add(new BookImageDto("Test Image.png"));

        List<ReviewDto> reviewDtoList = new ArrayList<>();
        reviewDtoList.add(new ReviewDto(1L, "test-review-content", 4, LocalDateTime.now(), "test user", new ArrayList<>()));


        Long bookId = 1L;
        BookDetailsDto mockBookDetails = new BookDetailsDto();
        mockBookDetails.setBookId(bookId);
        mockBookDetails.setTitle("Test Book");
        mockBookDetails.setIsbn("Test Isbn");
        mockBookDetails.setRegularPrice(20000);
        mockBookDetails.setSalePrice(15000);
        mockBookDetails.setExplanation("Test Explanation");
        mockBookDetails.setContents("Test Contents");
        mockBookDetails.setPublishDate(LocalDate.now());
        mockBookDetails.setPublisher(new PublisherDto(1L, "Test Publish"));
        mockBookDetails.setBookImages(bookImageDtoList);
        mockBookDetails.setReviews(reviewDtoList);
        mockBookDetails.setTagNames(new ArrayList<>());
        mockBookDetails.setLikeCount(100);
        mockBookDetails.setCategoryPath(new ArrayList<>());
        mockBookDetails.setRating(4.5);


        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookRepository.findBookDetailsByBookId(bookId)).thenReturn(mockBookDetails);


        // When
        BookDetailsDto result = bookService.getBookDetails(bookId);



        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getTitle()).isEqualTo("Test Book");
        assertThat(result.getExplanation()).isEqualTo("Test Explanation");
        assertThat(result.getLikeCount()).isEqualTo(100);
        assertThat(result.getPublisher().getName()).isEqualTo("Test Publish");
        assertThat(result.getRating()).isEqualTo(4.0); // 평균 평점 검증
        assertThat(result.getReviews().size()).isEqualTo(1);
        assertThat(result.getBookImages().size()).isEqualTo(1);
        assertThat(result.getReviews().getFirst().getUserName()).isEqualTo("test user");

        verify(bookRepository).existsById(bookId);
        verify(bookRepository).findBookDetailsByBookId(bookId);
    }

    @Test
    void testGetBookDetails_BookNotFound() {
        // Given
        Long bookId = 1L;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> bookService.getBookDetails(bookId))
                .isInstanceOf(NotFoundBookException.class)
                .hasMessageContaining(String.valueOf(bookId));

        verify(bookRepository).existsById(bookId);
        verify(bookRepository, never()).findBookDetailsByBookId(anyLong());
    }
}
