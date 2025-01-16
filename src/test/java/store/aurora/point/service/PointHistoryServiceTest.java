package store.aurora.point.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.*;
import store.aurora.order.entity.Order;
import store.aurora.order.service.OrderDetailService;
import store.aurora.point.dto.PointHistoryResponse;
import store.aurora.point.entity.*;
import store.aurora.point.exception.InvalidPointPolicyException;
import store.aurora.point.repository.PointHistoryRepository;
import store.aurora.user.entity.User;
import store.aurora.user.service.impl.UserRankService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PointPolicyService pointPolicyService;

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private UserRankService userRankService;

    static String userId = "user1";
    static PointPolicy pointPolicy;
    static PointHistory history1, history2;
    static User user = new User(userId, "John Doe", LocalDate.of(1990, 1, 1), "010-1234-5678", "test@example.com", false);

    @BeforeAll
    static void setUp() {
        pointPolicy = new PointPolicy(PointPolicyCategory.REVIEW, "Policy1", PointPolicyType.AMOUNT, BigDecimal.valueOf(100));
        history1 = new PointHistory(50, PointType.EARNED, user, pointPolicy);
        history2 = new PointHistory(-20, PointType.USED, user, pointPolicy);
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

    @Test
    void earnReviewPoint_shouldSaveReviewPoint() {

        when(pointPolicyService.getActivePoliciesByCategory(PointPolicyCategory.REVIEW))
                .thenReturn(List.of(pointPolicy));

        // Act
        pointHistoryService.earnReviewPoint(user, false);

        // Assert
        verify(pointHistoryRepository, times(1)).save(
                argThat(pointHistory -> {
                    assertThat(pointHistory.getPointAmount()).isEqualTo(100);
                    assertThat(pointHistory.getPointType()).isEqualTo(PointType.EARNED);
                    assertThat(pointHistory.getUser()).isEqualTo(user);
                    return true;
                })
        );
    }

    @Test
    void earnOrderPoints_shouldSaveOrderPointHistory() {
        // Arrange
        Order mockOrder = new Order();
        mockOrder.setUser(user);
        mockOrder.setTotalAmount(500);

        when(orderDetailService.getTotalWrapCostByOrder(mockOrder.getId())).thenReturn(50);
        when(userRankService.getCurrentPointRateByUserId(user.getId()))
                .thenReturn(Optional.of(BigDecimal.valueOf(0.1)));

        // Act
        pointHistoryService.earnOrderPoints(mockOrder);

        // Assert
        verify(pointHistoryRepository, times(1)).save(
                argThat(pointHistory -> {
                    assertThat(pointHistory.getPointAmount()).isEqualTo(45); // (500 - 50) * 0.1
                    assertThat(pointHistory.getPointType()).isEqualTo(PointType.EARNED);
                    assertThat(pointHistory.getUser()).isEqualTo(user);
                    return true;
                })
        );
    }

    @Test
    void earnOrderPoints_shouldThrowInvalidPointPolicyException_whenPointRateNotFound() {
        // Arrange
        Order mockOrder = new Order();
        mockOrder.setUser(user);
        mockOrder.setTotalAmount(500);

        when(orderDetailService.getTotalWrapCostByOrder(mockOrder.getId())).thenReturn(50);
        when(userRankService.getCurrentPointRateByUserId(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pointHistoryService.earnOrderPoints(mockOrder))
                .isInstanceOf(InvalidPointPolicyException.class);

        verifyNoInteractions(pointHistoryRepository);
    }

    @Test
    void earnPoint_shouldSavePointHistoryForPolicy() {
        when(pointPolicyService.getActivePoliciesByCategory(PointPolicyCategory.SIGNUP))
                .thenReturn(List.of(pointPolicy));

        // Act
        pointHistoryService.earnPoint(PointPolicyCategory.SIGNUP, user);

        // Assert
        verify(pointHistoryRepository, times(1)).save(
                argThat(pointHistory -> {
                    assertThat(pointHistory.getPointAmount()).isEqualTo(100);
                    assertThat(pointHistory.getPointType()).isEqualTo(PointType.EARNED);
                    assertThat(pointHistory.getUser()).isEqualTo(user);
                    return true;
                })
        );
    }

    @Test
    void earnPoint_shouldThrowInvalidPointPolicyException_whenPolicyTypeIsInvalid() {
        // Arrange
        PointPolicy mockPolicy = new PointPolicy(PointPolicyCategory.SIGNUP, "Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(100));

        when(pointPolicyService.getActivePoliciesByCategory(PointPolicyCategory.SIGNUP))
                .thenReturn(List.of(mockPolicy));

        // Act & Assert
        assertThatThrownBy(() -> pointHistoryService.earnPoint(PointPolicyCategory.SIGNUP, user))
                .isInstanceOf(InvalidPointPolicyException.class);

        verifyNoInteractions(pointHistoryRepository);
    }
}