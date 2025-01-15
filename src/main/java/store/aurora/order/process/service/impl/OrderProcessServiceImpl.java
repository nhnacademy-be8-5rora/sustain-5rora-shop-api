package store.aurora.order.process.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Book;
import store.aurora.book.service.BookService;
import store.aurora.order.dto.*;
import store.aurora.order.entity.*;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.PaymentState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.process.service.DeliveryFeeService;
import store.aurora.order.process.service.OrderInfoService;
import store.aurora.order.service.*;
import store.aurora.order.process.service.OrderProcessService;
import store.aurora.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderProcessServiceImpl implements OrderProcessService {
    private final BookService bookService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ShipmentInformationService shipmentInformationService;
    private final ShipmentService shipmentService;
    private final WrapService wrapService;
    private final DeliveryFeeService deliveryFeeService;
    private final OrderInfoService orderInfoService;
    private final UserService userService;
    private final PaymentService paymentService;

    @Override
    public String getOrderUuid(){
        return UUID.randomUUID().toString();
    }

    // todo: point 사용량 파라미터로 받아서 적용해야 함
    @Override
    public int getTotalAmountFromOrderDetailList(List<OrderDetailDTO> orderDetailList) {
        int totalAmount = 0;
        for (OrderDetailDTO detail : orderDetailList) {
            // 책 가격
            int bookSalePrice = bookService.getBookById(detail.getBookId()).getSalePrice();

            // 책 가격 계산
            int amount = bookSalePrice * detail.getQuantity();

            // wrap 금액 적용
            if(Objects.nonNull(detail.getWrapId()))
                amount += wrapService.getWrap(detail.getWrapId()).getAmount()
                        * detail.getQuantity();

            // 할인 금액 적용
            if(Objects.nonNull(detail.getDiscountAmount()))
                amount -= detail.getDiscountAmount();

            totalAmount += amount;
        }

        // 배송비 계산
        totalAmount += deliveryFeeService.getDeliveryFee(totalAmount);

        return totalAmount;
    }

    @Override
    public void saveOrderInfoInRedisWithUuid(String uuid, OrderRequestDto orderInfo){
        orderInfoService.saveOrderInfoInRedisWithUuid(uuid, orderInfo);
    }

    // todo: Coupon api 에 요청하는 로직 보내서 할인 금액 계산 / 계산된 할인 금액 받아오기
    public int getBookDiscountAmountFromDiscountPolicy(int bookSalePrice){
        return bookSalePrice * 10 / 100;
    }

    @Override
    public OrderResponseDto getOrderResponseFromOrderRequestDtoInRedis(String uuid){
        OrderResponseDto response = new OrderResponseDto();

        OrderRequestDto dto = orderInfoService.getOrderInfoFromRedis(uuid);

        String customerKey = UUID.randomUUID().toString();
        String currency = "KRW";

        /*
            OrderDetailList 를 통해서 다음 내용 ( value, orderName ) 생성함

            todo: OrderDetailDto마다 쿠폰 할인 금액 적용
             for-each : orderDetailList
                discountAmount = coupon 할인가
         */
        List<OrderDetailDTO> orderDetailList = Objects.requireNonNull(dto).getOrderDetailDTOList();

        int value = getTotalAmountFromOrderDetailList(orderDetailList)
                    - dto.getUsedPoint();

        StringBuilder orderName = new StringBuilder();
        for(OrderDetailDTO detail : orderDetailList){
            orderName.append(detail.getBookId())
                    .append(detail.getQuantity())
                    .append(Objects.nonNull(detail.getWrapId()) ? detail.getWrapId() : "")
                    .append(Objects.nonNull(detail.getCouponId()) ? detail.getCouponId() : "");
        }

        // response setting
        response.setCustomerKey(customerKey);
        response.setCurrency(currency);
        response.setValue(value);
        response.setOrderName(orderName.toString());

        return response;
    }

    // todo-3 장바구니 물품 주문한 경우, 주문한 책 장바구니에서 지우는 로직 추가
    public void userOrderProcess(String redisOrderId, String paymentKey, int amount){
        OrderRequestDto orderInfo = orderInfoService.getOrderInfoFromRedis(redisOrderId);

        int deliveryFee = deliveryFeeService.getDeliveryFee(amount);

        Order newOrder = Order.builder()
                .deliveryFee(deliveryFee)
                .orderTime(LocalDateTime.now())
                .totalAmount(amount - deliveryFee)
                .pointAmount(orderInfo.getUsedPoint())
                .state(OrderState.PENDING)
                .name(orderInfo.getOrdererName())
                .orderPhone(orderInfo.getOrdererPhone())
                .orderEmail(orderInfo.getOrdererEmail())
//                .preferredDeliveryDate(orderInfo.getPreferredDeliveryDate())
                .user(userService.getUser(orderInfo.getUsername()))
                .build();

        saveInformationWhenOrderComplete(newOrder, paymentKey, amount, orderInfo);
    }

    // todo: 비밀번호 passwordEncoder 적용
    @Override
    public void nonUserOrderProcess(String redisOrderId, String paymentKey, int amount){
        OrderRequestDto orderInfo = orderInfoService.getOrderInfoFromRedis(redisOrderId);

        int deliveryFee = deliveryFeeService.getDeliveryFee(amount);

        Order newOrder = Order.builder()
                .deliveryFee(deliveryFee)
                .orderTime(LocalDateTime.now())

                .totalAmount(amount - deliveryFee)
                .pointAmount(orderInfo.getUsedPoint())

                .state(OrderState.PENDING)

                .name(orderInfo.getOrdererName())
                .orderPhone(orderInfo.getOrdererPhone())
                .orderEmail(orderInfo.getOrdererEmail())

//                .preferredDeliveryDate(orderInfo.getPreferredDeliveryDate())
                .password(orderInfo.getNonMemberPassword())
                .build();

        saveInformationWhenOrderComplete(newOrder, paymentKey, amount, orderInfo);
    }

    // todo: 0원 결제 처리 로직 작성
    private void saveInformationWhenOrderComplete(Order order, String paymentKey, int amount, OrderRequestDto orderInfo){
        Order createdOrder = orderService.createOrder(order);

        // 배송 정보 생성
        Shipment shipment = shipmentService.createShipment(
                Shipment.builder()
                        .state(ShipmentState.PENDING)
                        .build()
        );

        // Order detail 생성
        for (OrderDetailDTO detailDTO : orderInfo.getOrderDetailDTOList()) {
            Book book = bookService.getBookById(detailDTO.getBookId());
            Wrap wrap = Objects.nonNull(detailDTO.getWrapId()) ? wrapService.getWrap(detailDTO.getWrapId()) : null;

            OrderDetail detail = OrderDetail.builder()
                    .order(createdOrder)
                    .state(createdOrder.getState())
                    .amountDetail(book.getSalePrice())
                    .quantity(detailDTO.getQuantity())
                    .wrap(wrap)
                    .couponId(detailDTO.getCouponId())
                    .book(book)
                    .shipment(shipment)
                    .build();

            orderDetailService.createOrderDetail(detail);
        }

        // 배송지 정보 생성
        ShipmentInformation info = ShipmentInformation.builder()
                .order(createdOrder)
                .receiverName(orderInfo.getReceiverName())
                .receiverPhone(orderInfo.getReceiverPhone())
                .receiverAddress(orderInfo.getReceiverAddress())
                .build();

        shipmentInformationService.createShipmentInformation(info);

        // Payment Key 에 결제 api 이름 추가? (ex. TOSS_1234)
        Payment payment = Payment.builder()
                .amount(amount)
                .paymentDatetime(createdOrder.getOrderTime())
                .status(PaymentState.COMPLETED)
                .order(createdOrder)
                .paymentKey(paymentKey)
                .build();

        paymentService.createPayment(payment);
    }
}
