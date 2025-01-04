package store.aurora.book.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Like;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.LikeRepository;
import store.aurora.book.service.impl.LikeServiceImpl;
import store.aurora.user.entity.User;
import store.aurora.user.repository.UserRepository;
import store.aurora.search.dto.BookSearchEntityDTO;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.user.exception.NotFoundUserException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Book book;
    private User user;
    private Like like;

    @BeforeEach
    void setUp() {
        // Book, User 객체 초기화
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        user = new User();
        user.setId("user1");
        user.setName("Test User");

        like = new Like(book, user, true);  // 책에 대한 좋아요 상태를 true로 설정
    }

    @Test
    @DisplayName("이미 좋아요를 누른 책에서 좋아요 상태를 토글해야 한다.")
    void testPressLike_AlreadyLiked() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(likeRepository.existsLikeByBookAndUser(book, user)).thenReturn(true);
        when(likeRepository.findByUserAndBook(user, book)).thenReturn(like);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        // 좋아요 상태를 토글
        boolean result = likeService.pressLike(1L, "user1");

        assertTrue(result);
        assertFalse(like.isLike()); // 좋아요 상태가 토글되었는지 확인
    }

    @Test
    @DisplayName("처음 좋아요를 누를 때 좋아요 상태가 true여야 한다.")
    void testPressLike_FirstTime() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(likeRepository.existsLikeByBookAndUser(book, user)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        // 처음 좋아요를 누를 때
        boolean result = likeService.pressLike(1L, "user1");

        assertTrue(result);
        assertTrue(like.isLike()); // 처음에는 좋아요가 true여야 함
    }

    @Test
    @DisplayName("좋아요 취소가 정상적으로 처리되어야 한다.")
    void testCancelLike() {
        when(likeRepository.deleteByBookIdAndUserId(1L, "user1")).thenReturn(like);

        Like canceledLike = likeService.cancelLike(1L, "user1");

        assertNotNull(canceledLike);
        assertEquals(1L, canceledLike.getBook().getId());
        assertEquals("user1", canceledLike.getUser().getId());
    }

    @Test
    @DisplayName("좋아요가 눌려있으면 true를 반환해야 한다.")
    void testIsLiked() {
        when(likeRepository.findByUserIdAndBookId("user1", 1L)).thenReturn(like);

        boolean result = likeService.isLiked("user1", 1L);

        assertTrue(result);  // 좋아요가 눌려있으면 true 반환
    }

    @Test
    @DisplayName("좋아요가 눌려있지 않으면 false를 반환해야 한다.")
    void testIsNotLiked() {
        when(likeRepository.findByUserIdAndBookId("user1", 1L)).thenReturn(null);

        boolean result = likeService.isLiked("user1", 1L);

        assertFalse(result);  // 좋아요가 눌려있지 않으면 false 반환
    }


}
