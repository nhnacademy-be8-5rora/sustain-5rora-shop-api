package store.aurora.point.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.point.entity.*;
import store.aurora.point.exception.PointInsufficientException;
import store.aurora.point.repository.PointHistoryRepository;
import store.aurora.user.entity.User;
import store.aurora.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointSpendServiceTest {

    @InjectMocks
    private PointSpendService pointSpendService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserService userService;

    static String userId = "user1";
    static PointHistory history1, history2;
    static User user = new User(userId, "John Doe", LocalDate.of(1990, 1, 1), "010-1234-5678", "test@example.com", false);

    @BeforeAll
    static void setUp() {
        PointPolicy pointPolicy = new PointPolicy(PointPolicyCategory.REVIEW_IMAGE, "Policy1", PointPolicyType.PERCENTAGE, BigDecimal.valueOf(10.0));
        history1 = new PointHistory(50, PointType.EARNED, user, pointPolicy);
        history2 = new PointHistory(-20, PointType.USED, user, pointPolicy);
    }

    @Test
    @DisplayName("getAvailablePointsByUser: Should calculate total available points for a user")
    void testGetAvailablePointsByUser() {
        when(pointHistoryRepository.findByUserId(userId)).thenReturn(List.of(history1, history2));

        // When
        Integer result = pointSpendService.getAvailablePointsByUser(userId);

        // Then
        assertThat(result).isEqualTo(30);
        verify(pointHistoryRepository, times(1)).findByUserId(userId);
    }

    @Test
    void spendPoints_shouldSavePointHistory_whenPointsAreSufficient() {
        // Arrange
        int pointAmount = 50;
        List<PointHistory> mockHistories = List.of(
                new PointHistory(100, PointType.EARNED, null),
                new PointHistory(-30, PointType.USED, null)
        );

        Mockito.when(userService.getUser(userId)).thenReturn(user);
        Mockito.when(pointHistoryRepository.findByUserId(userId)).thenReturn(mockHistories);

        // Act
        pointSpendService.spendPoints(userId, pointAmount);

        // Assert
        Mockito.verify(pointHistoryRepository, Mockito.times(1))
                .save(ArgumentMatchers.argThat(history ->
                        history.getPointAmount() == -pointAmount &&
                                history.getPointType() == PointType.USED &&
                                history.getUser().equals(user)
                ));
    }

    @Test
    void spendPoints_shouldThrowPointInsufficientException_whenPointsAreInsufficient() {
        // Arrange
        int pointAmount = 200;
        List<PointHistory> mockHistories = List.of(
                new PointHistory(100, PointType.EARNED, null),
                new PointHistory(-50, PointType.USED, null)
        );

        Mockito.when(pointHistoryRepository.findByUserId(userId)).thenReturn(mockHistories);

        // Act & Assert
        Assertions.assertThrows(PointInsufficientException.class, () -> pointSpendService.spendPoints(userId, pointAmount));

        Mockito.verify(pointHistoryRepository, Mockito.never()).save(Mockito.any(PointHistory.class));
    }
}