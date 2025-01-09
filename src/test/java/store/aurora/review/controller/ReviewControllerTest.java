package store.aurora.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.review.dto.ReviewRequest;
import store.aurora.review.service.ReviewService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
    }

    // 리뷰 등록 테스트
//    @Test
//    void createReview_shouldReturnStatus201_whenValidRequest() throws Exception {
//        // Given
//        ReviewRequest reviewRequest = new ReviewRequest(5, "Great book!");
//
//        // When & Then
//        mockMvc.perform(post("/api/reviews")
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .param("bookId", "1")
//                        .param("userId", "user1")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(content().string("Upload OK"));
//
//        // Verify that the service method was called
//        verify(reviewService, times(1)).saveReview(any(), any(), anyLong(), anyString());
//    }

    // 도서 ID로 리뷰 조회 테스트
    @Test
    void getReviewsByBookId_shouldReturnReviews_whenValidBookId() throws Exception {
        // Given
        when(reviewService.getReviewsByBookId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/reviews/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        // Verify that the service method was called
        verify(reviewService, times(1)).getReviewsByBookId(1L);
    }

    // 사용자 ID로 리뷰 조회 테스트
    @Test
    void getReviewsByUserId_shouldReturnReviews_whenValidUserId() throws Exception {
        // Given
        when(reviewService.getReviewsByUserId("user1")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/reviews/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        // Verify that the service method was called
        verify(reviewService, times(1)).getReviewsByUserId("user1");
    }

    // 리뷰 수정 테스트
//    @Test
//    void updateReview_shouldReturnStatus200_whenValidRequest() throws Exception {
//        // Given
//        ReviewRequest reviewRequest = new ReviewRequest(5, "Updated review content");
//
//        // When & Then
//        mockMvc.perform(put("/api/reviews/{reviewId}/edit", 1L)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .param("bookId", "1")
//                        .param("userId", "user1")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Review updated successfully"));
//
//        // Verify that the service method was called
//        verify(reviewService, times(1)).updateReview(eq(1L), any(), any(), anyLong(), anyString());
//    }
//
//    // 실패 시 테스트 (IOException 예시)
//    @Test
//    void createReview_shouldReturnStatus500_whenIOExceptionOccurs() throws Exception {
//        // Given
//        ReviewRequest reviewRequest = new ReviewRequest(5, "Great book!");
//
//        doThrow(new IOException("Upload failed")).when(reviewService).saveReview(any(), any(), anyLong(), anyString());
//
//        // When & Then
//        mockMvc.perform(post("/api/reviews")
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .param("bookId", "1")
//                        .param("userId", "user1")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(content().string("Upload failed: Upload failed"));
//
//        // Verify that the service method was called
//        verify(reviewService, times(1)).saveReview(any(), any(), anyLong(), anyString());
//    }
}
