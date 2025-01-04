package store.aurora.book.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.dto.BookDetailsDto;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookImageDto;
import store.aurora.book.dto.PublisherDto;
import store.aurora.book.dto.ReviewDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Like;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.LikeRepository;
import store.aurora.book.service.impl.BookServiceImpl;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Mock
    private LikeRepository likeRepository;

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

    @Test
    @DisplayName("사용자가 좋아요를 누른 책 목록을 가져오는 테스트")
    void testGetBooksByLike_Success() {
        // Given
        String userId = "user123";
        PageRequest pageable = PageRequest.of(0, 8);

        // 1. Publisher와 Series 객체 생성
        Publisher publisher = new Publisher(1L, "Publisher Name");
        Series series = new Series(1L, "Series Name");

        // 2. Book 객체 생성
        Book book1 = new Book(1L, "Book Title 1", 10000, 8000, 100, true, "ISBN12345", "Contents", "Explanation", false, LocalDate.now(), publisher, series, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Book book2 = new Book(2L, "Book Title 2", 15000, 12000, 200, true, "ISBN67890", "Contents", "Explanation", false, LocalDate.now(), publisher, series, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // 3. User 객체 생성
        User user = new User("user123", "John Doe", LocalDate.of(1990, 1, 1), "010-1234-5678", "john.doe@example.com", false);

        // 4. Like 객체 생성
        Like like1 = new Like(book1, user, true);
        Like like2 = new Like(book2, user, true);
        List<Like> likes = Arrays.asList(like1, like2);

        // 5. Mocking likeRepository
        when(likeRepository.findByUserIdAndIsLikeTrue(userId)).thenReturn(likes);

        // 6. BookSearchEntityDTO 객체 생성 (BookSearchEntityDTO에서 변환된 객체들)
        BookSearchEntityDTO book1Dto = new BookSearchEntityDTO(
                1L, "Book Title 1", 10000, 8000, LocalDate.of(2023, 5, 15), "Publisher Name",
                "Author Name (Author Role), Another Author (Editor)", "path/to/image1.jpg", "1,2,3", 100L, 10, 4.5
        );
        BookSearchEntityDTO book2Dto = new BookSearchEntityDTO(
                2L, "Book Title 2", 15000, 12000, LocalDate.of(2022, 8, 22), "Another Publisher",
                "Author Two (Writer), Third Author", "path/to/image2.jpg", "4,5", 200L, 20, 4.0
        );


        // 7. Page<BookSearchEntityDTO> 생성
        Page<BookSearchEntityDTO> page = new PageImpl<>(Arrays.asList(book1Dto, book2Dto), pageable, 2);

        // 8. Mocking bookRepository
        when(bookRepository.findBookByIdIn(Arrays.asList(1L, 2L), pageable)).thenReturn(page);

        // 10. 결과 확인
        Page<BookSearchResponseDTO> result = bookService.getBooksByLike(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Book Title 1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("Book Title 2");

        // verify
        verify(likeRepository).findByUserIdAndIsLikeTrue(userId);
        verify(bookRepository).findBookByIdIn(Arrays.asList(1L, 2L), pageable);
    }



    @Test
    @DisplayName("사용자가 좋아요를 누른 책 목록이 없을 때")
    void testGetBooksByLike_NoBooksFound() {
        // Given
        String userId = "user123";
        PageRequest pageable = PageRequest.of(0, 8);

        List<Like> likes = Arrays.asList(); // 좋아요한 책이 없음
        when(likeRepository.findByUserIdAndIsLikeTrue(userId)).thenReturn(likes);

        // When
        Page<BookSearchResponseDTO> result = bookService.getBooksByLike(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty(); // 결과가 비어있어야 함

        verify(likeRepository).findByUserIdAndIsLikeTrue(userId);
        verify(bookRepository, never()).findBookByIdIn(any(), any()); // 책을 찾는 메서드는 호출되지 않음
    }
}
