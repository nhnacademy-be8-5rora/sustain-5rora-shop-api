package store.aurora.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.order.dto.OrderInfoDto;

public interface UserOrderInfoService {
    Page<OrderInfoDto> getOrderInfos(String userId, Pageable pageable);
}
