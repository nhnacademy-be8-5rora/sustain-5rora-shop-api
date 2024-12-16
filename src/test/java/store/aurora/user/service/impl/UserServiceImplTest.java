package store.aurora.user.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.entity.Role;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserRole;
import store.aurora.user.exception.RoleNotFoundException;
import store.aurora.user.repository.UserRankHistoryRepository;
import store.aurora.user.repository.UserRankRepository;
import store.aurora.user.repository.UserRepository;
import store.aurora.user.service.DoorayMessengerService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserRankRepository userRankRepository;
    @Mock private UserRankHistoryRepository userRankHistoryRepository;
    @Mock private DoorayMessengerService doorayMessengerService;
    @Mock private RedisTemplate redisTemplate;

    @InjectMocks private UserServiceImpl userService;

    private User testUser;
    private UserRole testUserRole;
    private Role testRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("testId", "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
        testUser.setPassword("password");

        testRole = new Role("USER");
        testUserRole = new UserRole(testUser, testRole);
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
}
