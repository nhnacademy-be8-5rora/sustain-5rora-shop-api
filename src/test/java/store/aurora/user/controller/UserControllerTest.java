package store.aurora.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.user.dto.UserResponseDto;
import store.aurora.user.service.DoorayMessengerService;
import store.aurora.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private DoorayMessengerService doorayMessengerService;

    @Test
    public void getUser_ShouldReturnUser_WhenUserIdExists() throws Exception {
        // given
        String userId = "hyewon";
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
}