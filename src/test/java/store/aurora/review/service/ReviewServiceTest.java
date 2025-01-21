//package store.aurora.review.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import store.aurora.book.entity.Book;
//import store.aurora.book.exception.book.BookNotFoundException;
//import store.aurora.book.repository.book.BookRepository;
//import store.aurora.order.repository.OrderDetailRepository;
//import store.aurora.review.dto.ReviewRequest;
//import store.aurora.review.dto.ReviewResponse;
//import store.aurora.review.entity.Review;
//import store.aurora.review.entity.ReviewImage;
//import store.aurora.review.exception.ReviewAlreadyExistsException;
//import store.aurora.review.exception.UnauthorizedReviewException;
//import store.aurora.review.repository.ReviewRepository;
//import store.aurora.user.entity.User;
//import store.aurora.user.exception.UserNotFoundException;
//import store.aurora.user.repository.UserRepository;
//import store.aurora.file.ObjectStorageService;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceTest {
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private OrderDetailRepository orderDetailRepository;
//
//    @Mock
//    private ObjectStorageService objectStorageService;
//
//    @Mock
//    private BookRepository bookRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    private Book book;
//    private User user;
//    private ReviewRequest reviewRequest;
//    private Review review;
//    private ReviewImage reviewImage;
//
//    @BeforeEach
//    void setUp() {
//        book = new Book(); // Book 객체 설정
//        book.setId(1L);
//
//        user = new User(); // User 객체 설정
//        user.setId("user1");
//
//        reviewRequest = new ReviewRequest(5, "Excellent!");
//
//        // Review 객체 설정
//        review = new Review();
//        review.setReviewRating(5);
//        review.setReviewContent("Great book!");
//        review.setReviewCreateAt(LocalDateTime.now());
//        review.setBook(book);
//        review.setUser(user);
//
//        // ReviewImage 객체 설정
//        reviewImage = new ReviewImage();
//        reviewImage.setImageFilePath("/images/path1.png");
//        review.setReviewImages(List.of(reviewImage));
//    }
//
//    @Test
//    @DisplayName("saveReview: 정상적인 리뷰 등록")
//    void saveReview_shouldSaveReview_whenValidRequest() throws IOException {
//        // Given
//        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(book));
//        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(user));
//        when(orderDetailRepository.existsByOrderUserIdAndBookId("user1", 1L)).thenReturn(true);
//        when(reviewRepository.existsByBookIdAndUserId(book.getId(), user.getId())).thenReturn(false);
//
//        // When
//        reviewService.saveReview(reviewRequest, Collections.emptyList(),1L, "user1");
//
//        // Then
//        verify(reviewRepository, times(1)).save(any(Review.class));
//    }
//
//    @Test
//    @DisplayName("saveReview: 이미 리뷰를 작성한 경우 예외 발생")
//    void saveReview_shouldThrowReviewAlreadyExistsException_whenReviewAlreadyExists() throws IOException {
//        // Given
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
//        when(orderDetailRepository.existsByOrderUserIdAndBookId("user1", 1L)).thenReturn(true);
//        when(reviewRepository.existsByBookIdAndUserId(book.getId(), user.getId())).thenReturn(true);
//
//        // When & Then
//        assertThrows(ReviewAlreadyExistsException.class, () -> {
//            reviewService.saveReview(reviewRequest, Collections.emptyList(), 1L, "user1");
//        });
//    }
//
//    @Test
//    @DisplayName("saveReview: 도서를 구매하지 않은 사용자가 리뷰 작성 시 예외 발생")
//    void saveReview_shouldThrowInvalidOrderException_whenUserHasNotOrderedBook() {
//        // Given
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
//        when(orderDetailRepository.existsByOrderUserIdAndBookId("user1", 1L)).thenReturn(false);
//
//        // When & Then
//        assertThrows(UnauthorizedReviewException.class, () -> {
//            reviewService.saveReview(reviewRequest, Collections.emptyList(),1L, "user1");
//        });
//    }
//
//    @Test
//    @DisplayName("saveReview: 도서가 존재하지 않는 경우")
//    void saveReview_shouldThrowException_whenBookNotFound() {
//        // Given
//        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(BookNotFoundException.class, () -> {
//            reviewService.saveReview(reviewRequest, Collections.emptyList(),1L, "user1");
//        });
//    }
//
//    @Test
//    @DisplayName("saveReview: 사용자가 존재하지 않는 경우")
//    void saveReview_shouldThrowException_whenUserNotFound() {
//        // Given
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById("user1")).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(UserNotFoundException.class, () -> {
//            reviewService.saveReview(reviewRequest, Collections.emptyList(),1L, "user1");
//        });
//    }
//
//    @Test
//    @DisplayName("getReviewsByBookId: 특정 책의 리뷰 목록 반환")
//    void getReviewsByBookId_shouldReturnReviews_whenBookExists() {
//        // Given
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(reviewRepository.findByBook(book)).thenReturn(List.of(review));
//
//        // When
//        List<ReviewResponse> result = reviewService.getReviewsByBookId(1L);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//
//        ReviewResponse response = result.get(0);
//        assertEquals(5, response.getRating());
//        assertEquals("Great book!", response.getContent());
//        assertEquals(book.getId(), response.getBookId());
//        assertEquals(user.getId(), response.getUserId());
//        assertEquals(1, response.getImageFilePath().size());
//        assertEquals("/images/path1.png", response.getImageFilePath().get(0));
//
//        verify(bookRepository, times(1)).findById(1L);
//        verify(reviewRepository, times(1)).findByBook(book);
//
//    }
//
//    @Test
//    @DisplayName("getReviewsByUserId: 특정 사용자의 리뷰 목록 반환")
//    void getReviewsByUserId_shouldReturnReviews_whenUserExists() {
//        // Given
//        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
//        when(reviewRepository.findByUser(user)).thenReturn(List.of(review));
//
//        // When
//        List<ReviewResponse> result = reviewService.getReviewsByUserId("user1");
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//
//        ReviewResponse response = result.get(0);
//        assertEquals(5, response.getRating());
//        assertEquals("Great book!", response.getContent());
//        assertEquals(book.getId(), response.getBookId());
//        assertEquals(user.getId(), response.getUserId());
//        assertEquals(1, response.getImageFilePath().size());
//        assertEquals("/images/path1.png", response.getImageFilePath().get(0));
//
//        // Verify 호출 확인
//        verify(userRepository).findById("user1");
//        verify(reviewRepository).findByUser(user);
//    }

//    @Test
//    @DisplayName("updateReview: 리뷰 수정 (이미지 미포함)")
//    void updateReview_shouldUpdateReview_whenValidRequest() throws IOException {
//        // Given
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
//
//        reviewRequest = new ReviewRequest(4, "Updated Content");
//
//        // When
//        reviewService.updateReview(1L, reviewRequest, Collections.emptyList(), "user1");
//
//        // Then
//        assertEquals("Updated Content", review.getReviewContent());
//        assertEquals(4, review.getReviewRating());
//        assertEquals(0, review.getReviewImages().size());
//        verify(reviewRepository, times(1)).save(review);
//    }
//
//    @Test
//    @DisplayName("updateReview: 리뷰 수정 (이미지 포함)")
//    void updateReview_shouldUpdateReview_whenValidRequest_withImages() throws IOException {
//        // Given
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
//
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.isEmpty()).thenReturn(false);
//        when(objectStorageService.uploadObject(file)).thenReturn("uploaded-url");
//
//        ReviewRequest reviewRequest = new ReviewRequest(4, "Updated Content");
//
//        // When
//        reviewService.updateReview(1L, reviewRequest, Collections.singletonList(file), "user1");
//
//        // Then
//        assertEquals("Updated Content", review.getReviewContent());
//        assertEquals(4, review.getReviewRating());
//        assertEquals(1, review.getReviewImages().size());
//        assertEquals("uploaded-url", review.getReviewImages().getFirst().getImageFilePath());
//
//        verify(reviewRepository, times(1)).save(review);
//    }
//
//
//}
