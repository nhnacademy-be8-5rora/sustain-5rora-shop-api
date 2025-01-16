package store.aurora.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.entity.enums.OrderState;

public interface UserOrderInfoService {
    Page<OrderInfoDto> getOrderInfos(String userId, Pageable pageable);
    OrderWithOrderDetailResponse getOrderDetailInfos(Long orderId, String userId, String password);
    Boolean isOwner(Long orderId, String userId, String password);
    Long cancelOrder(Long orderId);
    Long requestRefund(Long orderId);
    Long resolveRefund(Long orderId);
    Page<OrderInfoDto> getOrderInfosByState(OrderState orderState, Pageable pageable);
}
