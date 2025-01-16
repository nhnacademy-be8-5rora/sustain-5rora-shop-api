package store.aurora.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;

public interface UserOrderInfoService {
    Page<OrderInfoDto> getOrderInfos(String userId, Pageable pageable);
    OrderWithOrderDetailResponse getOrderDetailInfos(Long orderId, String userId, String password);
    Boolean isOwner(Long orderId, String userId, String password);
    Long cancelOrder(Long orderId);
}
