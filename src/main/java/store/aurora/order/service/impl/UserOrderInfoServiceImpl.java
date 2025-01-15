package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Book;
import store.aurora.order.dto.*;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Wrap;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.service.UserOrderInfoService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


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

    @Transactional(readOnly = true)
    @Override
    public OrderWithOrderDetailResponse getOrderDetailInfos(Long orderId, String userId, String password) {
        Optional<OrderRelatedInfoWithAuth> optionalOrderRelatedInfoWithAuth = orderRepository.findOrderRelatedInfoByOrderId(orderId);
        if(optionalOrderRelatedInfoWithAuth.isEmpty()){
            throw new OrderNotFoundException(orderId);
        }
        OrderRelatedInfoWithAuth orderRelatedInfoWithAuth = optionalOrderRelatedInfoWithAuth.get();

        //검증
        if(!isValid(orderRelatedInfoWithAuth, userId, password)){
            throw new OrderNotFoundException(orderId);
        }
        //주문 정보
        OrderRelatedInfoDto orderRelatedInfoDto = new OrderRelatedInfoDto(orderRelatedInfoWithAuth);

        //주문 목록
        List<OrderDetail> orderDetailByOrderId = orderRepository.findOrderDetailByOrderId(orderId);
        log.info("od size={}", orderDetailByOrderId.size());
        List<OrderDetailInfoDto> orderDetailInfoList = orderDetailByOrderId.stream()
                .map(od -> {
                    Book book = od.getBook();
                    Wrap wrap = od.getWrap();
                    return new OrderDetailInfoDto(book.getTitle(), book.getSalePrice(), od.getQuantity(), od.getCouponId(),
                            Objects.nonNull(wrap) ? wrap.getName() : null, Objects.nonNull(wrap) ? wrap.getAmount() : null,
                            od.getState(), od.getShipment().getId());
                }).toList();

        OrderWithOrderDetailResponse orderWithOrderDetails = new OrderWithOrderDetailResponse(orderRelatedInfoDto, orderDetailInfoList);
        log.info("order info={}", orderWithOrderDetails);

        return orderWithOrderDetails;
    }

    private Boolean isValid(OrderRelatedInfoWithAuth orderRelatedInfoWithAuth, String userId, String password){
        if(userId == null && password != null){
            return orderRelatedInfoWithAuth.getPassword().equals(password);
        }
        else
            return password == null && orderRelatedInfoWithAuth.getUserId().equals(userId);
    }
}
