package store.aurora.point.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.point.dto.PointHistoryResponse;
import store.aurora.point.entity.PointType;
import store.aurora.point.service.PointHistoryService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private PointHistoryController pointHistoryController;

    @Mock
    private PointHistoryService pointHistoryService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pointHistoryController).build();
    }

    @Test
    @DisplayName("GET /api/points/history - Should return paginated point histories")
    void testGetPointHistory() throws Exception {
        // Given
        String userId = "user1";
        PageRequest pageRequest = PageRequest.of(0, 10);

        PointHistoryResponse response1 = PointHistoryResponse.builder()
                .id(1L)
                .pointAmount(50)
                .pointType(PointType.EARNED)
                .transactionDate(LocalDateTime.now())
                .from("Policy1")
                .build();

        PointHistoryResponse response2 = PointHistoryResponse.builder()
                .id(2L)
                .pointAmount(-20)
                .pointType(PointType.USED)
                .transactionDate(LocalDateTime.now())
                .from("Policy2")
                .build();

        Page<PointHistoryResponse> page = new PageImpl<>(List.of(response1, response2), pageRequest, 2);
        when(pointHistoryService.getPointHistoryByUser(userId, 0, 10)).thenReturn(page);


        // When / Then
        mockMvc.perform(get("/api/points/history")
                        .header("X-USER-ID", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].pointAmount").value(50))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].pointAmount").value(-20));

        verify(pointHistoryService, times(1)).getPointHistoryByUser(userId, 0, 10);
    }
}