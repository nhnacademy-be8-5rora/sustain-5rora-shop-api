package store.aurora.point.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.*;
import store.aurora.point.dto.PointHistoryResponse;
import store.aurora.point.entity.*;
import store.aurora.point.repository.PointHistoryRepository;
import store.aurora.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    static String userId = "user1";
    static PointHistory history1, history2;

    @BeforeAll
    static void setUp() {
        PointPolicy pointPolicy = new PointPolicy(PointPolicyCategory.REVIEW_IMAGE, "Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));
        history1 = new PointHistory(50, PointType.EARNED, new User(userId, "John Doe", LocalDate.of(1990, 1, 1), "010-1234-5678", "test@example.com", false), pointPolicy);
        history2 = new PointHistory(-20, PointType.USED, new User(userId, "John Doe", LocalDate.of(1990, 1, 1), "010-1234-5678", "test@example.com", false), pointPolicy);
    }


    @Test
    @DisplayName("getPointHistoryByUser: Should return paginated point history responses for a user")
    void testGetPointHistoryByUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "transactionDate"));

        Page<PointHistory> page = new PageImpl<>(List.of(history1, history2));
        when(pointHistoryRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        // When
        Page<PointHistoryResponse> result = pointHistoryService.getPointHistoryByUser(userId, 0, 2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getPointAmount()).isEqualTo(50);
        assertThat(result.getContent().get(1).getPointAmount()).isEqualTo(-20);
        verify(pointHistoryRepository, times(1)).findByUserId(userId, pageable);
    }
}