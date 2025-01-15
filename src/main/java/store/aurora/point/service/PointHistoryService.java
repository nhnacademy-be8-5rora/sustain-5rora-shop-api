package store.aurora.point.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.entity.Order;
import store.aurora.order.service.OrderDetailService;
import store.aurora.point.dto.PointHistoryResponse;
import store.aurora.point.entity.*;
import store.aurora.point.exception.InvalidPointPolicyException;
import store.aurora.point.repository.PointHistoryRepository;
import store.aurora.user.entity.User;
import store.aurora.user.service.impl.UserRankService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger("user-logger");

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyService pointPolicyService;
    private final OrderDetailService orderDetailService;
    private final UserRankService userRankService;

    public Page<PointHistoryResponse> getPointHistoryByUser(String userId, int page, int size) {
        // todo 혜원 고려 : 사용자 존재 체크 해야하나? mypage 이니까 사용자만 접근가능하니까 안해도 될 듯

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<PointHistory> pointHistories;

        pointHistories = pointHistoryRepository.findByUserId(userId, pageable);
        return pointHistories.map(this::mapToResponse);
    }

    @Transactional
    public void earnReviewPoint(User user, boolean hasImage) {
        PointPolicyCategory pointPolicyCategory;
        if(hasImage) {
            pointPolicyCategory = PointPolicyCategory.REVIEW_IMAGE;
        } else {
            pointPolicyCategory = PointPolicyCategory.REVIEW;
        }

        earnPoint(pointPolicyCategory, user);
    }

    @Transactional
    public void earnSignUpPoints(User user) {
        earnPoint(PointPolicyCategory.SIGNUP, user);
    }

    // todo 혜원 : 스케줄러로
    @Transactional//(propagation = Propagation.REQUIRES_NEW)
    public void earnOrderPoints(Order order) {
        // todo 혜원 : CONFIRMED 가 아니면 적립을 요청하면 안됨
//            if(order.getState() != OrderState.CONFIRMED) throw new

        // todo 혜원 : 기본 적립률 따로 하지 말기?

        Integer totalWrapCost = orderDetailService.getTotalWrapCostByOrder(order.getId());
        if(totalWrapCost == null) totalWrapCost = 0;
        int netAmount = order.getTotalAmount() - totalWrapCost;
        Optional<BigDecimal> pointRate = userRankService.getCurrentPointRateByUserId(order.getUser().getId());

        pointHistoryRepository.save(new PointHistory(
                (int) (netAmount*pointRate.orElseThrow(()->new InvalidPointPolicyException("d")).doubleValue()),
                PointType.EARNED, order.getUser(), order));
        LOG.info("Points added successfully: userId={}, points={}", order.getUser().getId(), netAmount);
    }

    private void earnPoint(PointPolicyCategory pointPolicyCategory, User user) {
        List<PointPolicy> policies = pointPolicyService.getActivePoliciesByCategory(pointPolicyCategory);
        if(policies.size() > 1) {
            LOG.warn("현재 {}에 해당하는 적용 가능한 포인트 정책이 여러개 이지만 하나만 적용되었습니다.", pointPolicyCategory);
        }

        PointPolicy pointPolicy = policies.getFirst();

        if (pointPolicy.getPointPolicyType() != PointPolicyType.AMOUNT) {
            throw new InvalidPointPolicyException(String.format("%s은 포인트 정책 타입으로 AMOUNT만 가능합니다.", pointPolicyCategory));
        }

        pointHistoryRepository.save(new PointHistory(pointPolicy.getPointPolicyValue().intValue(), PointType.EARNED, user, pointPolicy));
        LOG.info("Points added successfully: userId={}, points={}", user.getId(), pointPolicy.getPointPolicyValue().intValue());
    }

    private PointHistoryResponse mapToResponse(PointHistory pointHistory) {
        String from;

        if (pointHistory.getPointPolicy() == null || "결제".equals(pointHistory.getPointPolicy().getPointPolicyName())) {
            if (pointHistory.getOrder() != null) {
                from = String.valueOf(pointHistory.getOrder().getId());
            } else {
                from = pointHistory.getPointPolicy() == null ? "결제" : pointHistory.getPointPolicy().getPointPolicyName();
                LOG.error("{} no order", pointHistory.getId());
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
}