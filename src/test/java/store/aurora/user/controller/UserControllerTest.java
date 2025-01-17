package store.aurora.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.point.service.PointHistoryService;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.exception.UserNotFoundException;
import store.aurora.user.service.DoorayMessengerService;
import store.aurora.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PointHistoryService pointHistoryService;

    @MockBean
    private DoorayMessengerService doorayMessengerService;

    private ObjectMapper objectMapper = new ObjectMapper();
    String userId = "hyewon";

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 Date/Time 지원 추가
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserIdExists() throws Exception {
        // given
        UserResponseDto userResponseDto = new UserResponseDto("hyewon", "ROLE_USER");

        // Mocking UserService
        when(userService.getUserByUserId(userId)).thenReturn(userResponseDto);

        // when & then
        mockMvc.perform(get("/api/users/auth/me")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("hyewon"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testUpdateLastLogin_Success() throws Exception {
        // Given
        LocalDateTime lastLogin = LocalDateTime.now();

        doNothing().when(userService).updateLastLogin(userId, lastLogin);

        // When & Then
        mockMvc.perform(patch("/api/users/" + userId + "/last-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastLogin)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateLastLogin(userId, lastLogin);
    }

    @Test
    void testUpdateLastLogin_UserNotFound() throws Exception {
        // Given
        LocalDateTime lastLogin = LocalDateTime.now();

        doThrow(new UserNotFoundException("User not found")).when(userService).updateLastLogin(userId, lastLogin);

        // When & Then
        mockMvc.perform(patch("/api/users/" + userId + "/last-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastLogin)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateLastLogin(userId, lastLogin);
    }
}