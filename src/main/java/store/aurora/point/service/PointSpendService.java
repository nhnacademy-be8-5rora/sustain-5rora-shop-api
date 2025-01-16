package store.aurora.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.point.entity.PointHistory;
import store.aurora.point.entity.PointType;
import store.aurora.point.exception.PointInsufficientException;
import store.aurora.point.repository.PointHistoryRepository;
import store.aurora.user.entity.User;
import store.aurora.user.service.UserService;

@Service
@RequiredArgsConstructor
public class PointSpendService {
    private final UserService userService;
    private final PointHistoryRepository pointHistoryRepository;

    public Integer getAvailablePointsByUser(String userId) {
        return pointHistoryRepository.findByUserId(userId).stream()
                .mapToInt(PointHistory::getPointAmount)
                .sum();
    }

    public void spendPoints(String userId, int pointAmount) {
        User user = userService.getUser(userId);
        Integer availablePoints = getAvailablePointsByUser(userId);

        if (availablePoints < pointAmount) {
            throw new PointInsufficientException(pointAmount, availablePoints);
        }

        pointHistoryRepository.save(
                new PointHistory(-pointAmount, PointType.USED, user)
        );
    }
}