package store.aurora.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import store.aurora.point.dto.PointHistoryResponse;
import store.aurora.point.entity.PointHistory;
import store.aurora.point.repository.PointHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointHistoryService {
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");
    private final PointHistoryRepository pointHistoryRepository;

    public Page<PointHistoryResponse> getPointHistoryByUser(String userId, int page, int size) {
        // todo 혜원 고려 : 사용자 존재 체크 해야하나? mypage 이니까 사용자만 접근가능하니까 안해도 될 듯

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<PointHistory> pointHistories;

        pointHistories = pointHistoryRepository.findByUserId(userId, pageable);
        return pointHistories.map(this::mapToResponse);
    }

    private PointHistoryResponse mapToResponse(PointHistory pointHistory) {
        String from;

        if ("결제".equals(pointHistory.getPointPolicy().getPointPolicyName())) {
            if (pointHistory.getOrder() != null) {
                from = String.valueOf(pointHistory.getOrder().getId());
            } else {
                from = pointHistory.getPointPolicy().getPointPolicyName();
                USER_LOG.error("{} no order", pointHistory.getId());
            }

        } else {
            from = pointHistory.getPointPolicy().getPointPolicyName();
        }

        return PointHistoryResponse.builder()
                .id(pointHistory.getId())
                .pointAmount(pointHistory.getPointAmount())
                .pointType(pointHistory.getPointType())
                .transactionDate(pointHistory.getTransactionDate())
                .from(from)
                .build();
    }

    public Integer getAvailablePointsByUser(String userId) {
        List<PointHistory> pointHistories = pointHistoryRepository.findByUserId(userId);

        return pointHistories.stream()
                .mapToInt(PointHistory::getPointAmount)
                .sum();
    }
}
