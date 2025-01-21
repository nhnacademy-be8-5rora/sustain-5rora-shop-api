package store.aurora.book.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.book.service.like.LikeService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class LikeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    @DisplayName("좋아요 눌렀을 때 성공적인 좋아요 토글")
    void testDoLike() throws Exception {
        Long bookId = 1L;
        String userId = "user123";
        boolean isLiked = true;

        // 서비스 mock
        when(likeService.pressLike(bookId, userId)).thenReturn(isLiked);

        mockMvc.perform(post("/api/books/likes/{bookId}", bookId)
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(isLiked));

        verify(likeService, times(1)).pressLike(bookId, userId);
    }


}
