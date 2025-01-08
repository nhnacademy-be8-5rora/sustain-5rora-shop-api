package store.aurora.order.process.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.service.BookService;
import store.aurora.order.dto.*;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.ShipmentInformation;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.process.service.DeliveryFeeService;
import store.aurora.order.process.service.OrderInfoService;
import store.aurora.order.service.*;
import store.aurora.order.process.service.OrderProcessService;
import store.aurora.user.entity.User;

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

        // todo: 포인트 사용 금액 적용
        int value = getTotalAmountFromOrderDetailList(orderDetailList);

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

    // todo-1 Payment 처리 로직 추가
    // todo-2 사용하는 DTO 수정
    // todo-3 장바구니 물품 주문한 경우, 주문한 책 장바구니에서 지우는 로직 추가
    @Override
    public void userOrderProcess(OrderDTO order,
                                 List<OrderDetailDTO> orderDetailList,
                                 ReceiverInfoDTO receiverInfo,
                                 User user,
                                 OrderedPersonInfoDTO orderedPersonInfo){

        int totalAmount = getTotalAmountFromOrderDetailList(orderDetailList);
        Order newOrder = Order.builder()
                .deliveryFee(deliveryFeeService.getDeliveryFee(totalAmount))
                .orderTime(order.getOrderTime())
                .totalAmount(0)
                .pointAmount(order.getPointAmount())
                .state(OrderState.PENDING)
                .name(orderedPersonInfo.getName())
                .orderPhone(orderedPersonInfo.getPhone())
                .preferredDeliveryDate(order.getPreferredDeliveryDate())
                .user(user)
                .build();

        orderSuccessProcess(newOrder, orderDetailList, receiverInfo);
    }

    // todo-1 Payment 처리 로직 추가
    // todo-2 사용하는 DTO 수정
    @Override
    public void nonUserOrderProcess(OrderDTO order,
                                    List<OrderDetailDTO> orderDetailList,
                                    ReceiverInfoDTO receiverInfo,
                                    OrderedPersonInfoDTO orderedPersonInfo){

        int totalAmount = getTotalAmountFromOrderDetailList(orderDetailList);
        Order newOrder = Order.builder()
                .deliveryFee(deliveryFeeService.getDeliveryFee(totalAmount))
                .orderTime(order.getOrderTime())
                .totalAmount(0)
                .pointAmount(order.getPointAmount())
                .state(OrderState.PENDING)
                .name(orderedPersonInfo.getName())
                .orderPhone(orderedPersonInfo.getPhone())
                .preferredDeliveryDate(order.getPreferredDeliveryDate())
                .password(orderedPersonInfo.getPassword())
                .build();

        orderSuccessProcess(newOrder, orderDetailList, receiverInfo);
    }

    private void orderSuccessProcess(Order order,
                                     List<OrderDetailDTO> orderDetailList,
                                     ReceiverInfoDTO receiverInfo){

        Order createdOrder = orderService.createOrder(order);

        Shipment shipment = Shipment.builder()
                .state(ShipmentState.PENDING)
                .build();
        shipment = shipmentService.createShipment(shipment);

        for (OrderDetailDTO detailDTO : orderDetailList) {
            OrderDetail detail = OrderDetail.builder()
                    .order(createdOrder)
                    .state(createdOrder.getState())
                    .amountDetail(bookService.getBookById(detailDTO.getBookId()).getSalePrice() * detailDTO.getQuantity())
                    .quantity(detailDTO.getQuantity())
                    .wrap(wrapService.getWrap(detailDTO.getWrapId()))
                    .couponId(detailDTO.getCouponId())
                    .book(bookService.getBookById(detailDTO.getBookId()))
                    .shipment(shipment)
                    .build();

            orderDetailService.createOrderDetail(detail);
        }

        ShipmentInformation info = ShipmentInformation.builder()
                .order(createdOrder)
                .receiverName(receiverInfo.getName())
                .receiverPhone(receiverInfo.getPhone())
                .receiverAddress(receiverInfo.getAddress())
                .build();

        shipmentInformationService.createShipmentInformation(info);
    }
}
