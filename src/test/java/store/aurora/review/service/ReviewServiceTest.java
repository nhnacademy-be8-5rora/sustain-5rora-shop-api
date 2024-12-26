package store.aurora.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.entity.Book;
import store.aurora.book.repository.BookRepository;
import store.aurora.review.dto.ReviewRequest;
import store.aurora.review.entity.Review;
import store.aurora.review.exception.ReviewAlreadyExistsException;
import store.aurora.review.repository.ReviewRepository;
import store.aurora.user.entity.User;
import store.aurora.user.repository.UserRepository;
import store.aurora.file.ObjectStorageService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private Book book;
    private User user;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        book = new Book(); // Book 객체 설정
        book.setId(1L);

        user = new User(); // User 객체 설정
        user.setId("user1");

        reviewRequest = new ReviewRequest(5, "Excellent!", Collections.emptyList()); // 예시 리뷰 요청
    }

    @Test
    void saveReview_shouldSaveReview_whenValidRequest() throws IOException {
        // Given
        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(book));
        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(user));
        when(reviewRepository.existsByBookAndUser(book, user)).thenReturn(false);

        // When
        reviewService.saveReview(reviewRequest, 1L, "user1");

        // Then
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void saveReview_shouldThrowReviewAlreadyExistsException_whenReviewAlreadyExists() throws IOException {
        // Given
        when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(book));
        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(user));
        when(reviewRepository.existsByBookAndUser(book, user)).thenReturn(true);

        // When & Then
        assertThrows(ReviewAlreadyExistsException.class, () -> {
            reviewService.saveReview(reviewRequest, 1L, "user1");
        });
    }
}
