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
import store.aurora.order.entity.*;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.PaymentState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.repository.PaymentRepository;
import store.aurora.order.service.UserOrderInfoService;
import store.aurora.point.entity.PointHistory;
import store.aurora.point.entity.PointType;
import store.aurora.point.repository.PointHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserOrderInfoServiceImpl implements UserOrderInfoService {

    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PaymentRepository paymentRepository;

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

    @Transactional(readOnly = true)
    @Override
    public Boolean isOwner(Long orderId, String userId, String password) {
        Optional<Order> optionalOrder = orderRepository.findOrderWithUserByOrderId(orderId);
        if(optionalOrder.isEmpty() || (userId == null && password == null)){
            return false;
        }

        Order order = optionalOrder.get();
        //비회원
        if(order.getUser() == null){
            return order.getPassword().equals(password);
        }

        //회원
        else{
            return order.getUser().getId().equals(userId);
        }
    }

    @Transactional
    @Override
    public Long cancelOrder(Long orderId) {
        //1. 주문의 상태 바꾸기
        Order order = orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setState(OrderState.CANCELLED);

        //2. 주문 상세의 상태 바꾸기
        order.getOrderDetails().forEach(od -> od.setState(OrderState.CANCELLED));

        //3. 배송의 상태 바꾸기
        order.getOrderDetails().getFirst().getShipment().setState(ShipmentState.CANCELLED);

        //4. 결제 내역 가져와서 결제된 만큼을 결제내역에 추가
        int paidAmount = order.getPayments().stream().filter(od -> od.getAmount() > 0 ).findFirst().orElseThrow().getAmount();
        Payment payment = Payment.builder()
                .amount(-1 * paidAmount)
                .paymentDatetime(LocalDateTime.now())
                .status(PaymentState.COMPLETED)
                .order(order)
                .paymentKey(null)
                .build();
        paymentRepository.save(payment);

        //5. 포인트로 환불
        PointHistory pointHistory = new PointHistory(paidAmount, PointType.EARNED, order.getUser());
        pointHistoryRepository.save(pointHistory);

        return order.getId();
    }

    @Transactional
    @Override
    public Long requestRefund(Long orderId) {

        //주문 상태 변경
        Order order = orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setState(OrderState.REFUND_PENDING);

        //주문 상세 상태 변경
        order.getOrderDetails().forEach(od -> od.setState(OrderState.REFUND_PENDING));

        return order.getId();
    }


    @Transactional
    @Override
    public Long resolveRefund(Long orderId) {

        //주문 상태 변경
        Order order = orderRepository.findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setState(OrderState.REFUNDED);

        //주문 상세 상태 변경
        order.getOrderDetails().forEach(od -> od.setState(OrderState.REFUNDED));

        // 결제 내역 가져와서 결제된 만큼을 결제내역에 추가
        int paidAmount = order.getPayments().stream().filter(od -> od.getAmount() > 0 ).findFirst().orElseThrow().getAmount();
        Payment payment = Payment.builder()
                .amount(-1 * paidAmount)
                .paymentDatetime(LocalDateTime.now())
                .status(PaymentState.COMPLETED)
                .order(order)
                .paymentKey(null)
                .build();
        paymentRepository.save(payment);

        //포인트로 환불
        PointHistory pointHistory = new PointHistory(paidAmount, PointType.EARNED, order.getUser());
        pointHistoryRepository.save(pointHistory);

        return order.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderInfoDto> getOrderInfosByState(OrderState orderState, Pageable pageable) {
        return orderRepository.findOrderInfosByOrderState(orderState.ordinal(), pageable)
                .map(oi ->
                    new OrderInfoDto(
                        oi.getOrderId(),
                        oi.getTotalAmount(),
                        OrderState.fromOrdinal(oi.getOrderState()),
                        oi.getOrderTime(),
                        oi.getOrderContent()
                    )
                );
    }


    private Boolean isValid(OrderRelatedInfoWithAuth orderRelatedInfoWithAuth, String userId, String password){
        if(userId == null && password != null){
            return orderRelatedInfoWithAuth.getPassword().equals(password);
        }
        else
            return password == null && orderRelatedInfoWithAuth.getUserId().equals(userId);
    }
}
