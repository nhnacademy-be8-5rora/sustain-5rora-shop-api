package store.aurora.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.entity.Role;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserRole;
import store.aurora.user.exception.RoleNotFoundException;
import store.aurora.user.exception.UserNotFoundException;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserServiceImpl userService;

    private User testUser;
    String userId = "testId";

    @BeforeEach
    void setUp() {
        testUser = new User(userId, "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
        testUser.setPassword("password");

        Role testRole = new Role("USER");
        UserRole testUserRole = new UserRole(testUser, testRole);
        LinkedList<UserRole> list = new LinkedList<>();
        list.add(testUserRole);
        testUser.setUserRoles(list);

        // Bypass passwordEncoder initialization in UserServiceImpl
        ReflectionTestUtils.setField(userService, "passwordEncoder", new BCryptPasswordEncoder());

    }

    @Test
    void testGetUserByUserId_Success() {
        // Given
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));

        // When
        UserResponseDto result = userService.getUserByUserId("user1");

        // Then
        assertNotNull(result);
        assertEquals("testId", result.getUsername());
        assertEquals("USER", result.getRole());
    }

    @Test
    void testGetUserByUserId_RoleNotFound() {
        // Given
        User userWithoutRole = new User("user2", "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
        userWithoutRole.setUserRoles(new LinkedList<>());

        when(userRepository.findById("user2")).thenReturn(Optional.of(userWithoutRole));

        // When & Then
        assertThrows(RoleNotFoundException.class, () -> userService.getUserByUserId("user2"));
    }

    // todo 유민 회원가입 테스트
    @Test
    void registerUser_Success() {
        // Given
//        SignUpRequest request = new SignUpRequest(
//                "testId", "password123", "홍길동", "20000101", "01012345678", "test@example.com"
//        );


    }

    @Test
    void testUpdateLastLogin() {
        // Given: 테스트 사용자와 초기 데이터 설정
        LocalDateTime initialLogin = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime updatedLogin = LocalDateTime.of(2025, 1, 16, 12, 0);

        testUser.setLastLogin(initialLogin);

        // UserRepository에서 getUser 메서드 호출 시 user 반환하도록 설정
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When: updateLastLogin 호출
        userService.updateLastLogin(userId, updatedLogin);

        // Then: lastLogin 필드가 업데이트되었는지 검증
        assertThat(testUser.getLastLogin()).isEqualTo(updatedLogin);
    }

    @Test
    void testUpdateLastLogin_UserNotFound() {
        // Given: 존재하지 않는 사용자 ID
        LocalDateTime updatedLogin = LocalDateTime.of(2025, 1, 16, 12, 0);

        // UserRepository에서 getUser 메서드 호출 시 빈 Optional 반환
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then: 예외가 발생해야 함
        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateLastLogin(userId, updatedLogin)
        );
    }
}