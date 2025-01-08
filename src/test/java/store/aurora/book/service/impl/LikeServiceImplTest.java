package store.aurora.book.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Like;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.LikeRepository;
import store.aurora.book.service.impl.LikeServiceImpl;
import store.aurora.user.entity.User;
import store.aurora.user.repository.UserRepository;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.user.exception.NotFoundUserException;

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

        like = new Like(book, user, false);  // 책에 대한 좋아요 상태를 false로 설정
    }

    @Test
    @DisplayName("이미 좋아요를 눌렀던 책에서 좋아요 상태를 토글해야 한다.")
    void testPressLike_AlreadyLiked() {
        // Book, User 객체 설정
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        // 좋아요가 눌려있는 상태의 Like 객체를 반환
        when(likeRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(like));
        // Like 객체의 상태가 토글된 후 반환할 새로운 Like 객체를 설정
        Like toggledLike = new Like(book, user, true);  // 좋아요 상태가 true로 변경
        when(likeRepository.save(any(Like.class))).thenReturn(toggledLike);  // save가 토글된 Like 객체를 반환하도록 설정

        // 좋아요 상태를 토글
        boolean result = likeService.pressLike(1L, "user1");

        assertTrue(result);  // true를 반환해야 함
        assertTrue(toggledLike.isLike());  // 좋아요 상태가 true여야 함
        verify(likeRepository, times(1)).save(any(Like.class));  // save 메서드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("처음 좋아요를 눌렀을 때 좋아요 상태가 true여야 한다.")
    void testPressLike_FirstTime() {
        // Book, User 객체 설정
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        // 처음 좋아요를 눌렀을 때, Like 객체는 없으므로 Optional.empty()를 반환
        when(likeRepository.findByUserAndBook(user, book)).thenReturn(Optional.empty());
        // 새로 생성된 Like 객체가 반환될 수 있도록 설정
        Like newLike = new Like(book, user, true);
        when(likeRepository.save(any(Like.class))).thenReturn(newLike);  // save가 newLike를 반환하도록 설정

        // 처음 좋아요를 눌렀을 때
        boolean result = likeService.pressLike(1L, "user1");

        assertTrue(result);  // true를 반환해야 함
        assertTrue(newLike.isLike());  // 좋아요 상태가 true여야 함
        verify(likeRepository, times(1)).save(any(Like.class));  // save 메서드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("좋아요 취소가 정상적으로 처리되어야 한다.")
    void testCancelLike() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));  // 책 존재 여부 확인
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));  // 사용자 존재 여부 확인
        // 좋아요가 눌려있는 상태인 Like 객체를 반환
        when(likeRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(like));

        // 좋아요 취소 후 Like 객체의 상태를 변경
        like.setLike(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);  // 취소된 Like 객체를 반환하도록 설정

        // 좋아요 취소
        Like canceledLike = likeService.cancelLike(1L, "user1");

        assertNotNull(canceledLike);
        assertFalse(canceledLike.isLike());  // 좋아요 상태가 false여야 함
        verify(likeRepository, times(1)).save(any(Like.class));  // save 메서드가 한 번 호출된 횟수 확인
    }

}
