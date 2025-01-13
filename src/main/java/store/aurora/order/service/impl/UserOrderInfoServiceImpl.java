package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.dto.OrderInfo;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.service.UserOrderInfoService;


@Service
@RequiredArgsConstructor
public class UserOrderInfoServiceImpl implements UserOrderInfoService {

    private final OrderRepository orderRepository;

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    @Transactional(readOnly = true)
    @Override
    public Page<OrderInfoDto> getOrderInfos(String userId, Pageable pageable) {
        Page<OrderInfo> orderInfosByUserId = orderRepository.findOrderInfosByUserId(userId, pageable);

        Page<OrderInfoDto> orderInfoDtos = orderInfosByUserId.map(oi -> new OrderInfoDto(
                oi.getOrderId(),
                oi.getTotalAmount(),
                OrderState.fromOrdinal(oi.getOrderState()),
                oi.getOrderTime(),
                oi.getOrderContent()
        ));

        log.info("회원:{}의 주문목록={}", userId, orderInfoDtos.getContent());

        return orderInfoDtos;
    }
}
