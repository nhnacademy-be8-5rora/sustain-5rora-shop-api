package store.aurora.order.service.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import store.aurora.book.service.BookService;
import store.aurora.order.dto.*;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.ShipmentInformation;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.service.*;
import store.aurora.order.service.process.OrderProcessService;
import store.aurora.user.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProcessServiceImpl implements OrderProcessService {
    private final BookService bookService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ShipmentInformationService shipmentInformationService;
    private final ShipmentService shipmentService;
    private final WrapService wrapService;

    private final RedisTemplate<String, OrderRequestDto> orderRedisTemplate;

    /*
     *     todo 배송비 로직 수정
     *      배송비 정책이 수정될 경우를 고려해 setting 테이블에서 배송비 관련 정보를 가져오도록 수정해야 함
     *         이 로직으로는 배송비 정책을 수정할 때마다 코드를 수정하고, 서버를 재배포해야 한다.
     *         그래서 setting 테이블을 작성하여 관리하고자 함
     *         1단계. setting 테이블에 배송비 관련 정보를 저장하고, 배송비를 가져오는 로직으로 수정
     *         2단계. 매 주문마다 배송비를 setting에서 불러오는 것이 아닌 캐싱하여 사용
     *             2-1. 특정 시간마다 배송비를 캐싱하고, 배송비를 가져올 때 캐싱된 값을 사용
     *             2-2. 배송비 관련 정보가 변경되었을 때 캐싱된 값을 삭제하고, 새로운 값을 캐싱
     */
    @Override
    public int getDeliveryFee(int totalAmount) {
        int minAmount = 30000;

        int deliveryFee = 5000;
        if(totalAmount >= minAmount){
            deliveryFee = 0;
        }

        return deliveryFee;
    }

    @Override
    public int getTotalAmountFromOrderDetailList(List<OrderDetailDTO> orderDetailList) {
        int totalAmount = 0;
        for (OrderDetailDTO detail : orderDetailList) {
            int amount = bookService.getBookById(detail.getBookId()).getSalePrice()
                        * detail.getQuantity();

            // wrap 금액 적용
            if(Objects.nonNull(detail.getWrapId()))
                amount += wrapService.getWrap(detail.getWrapId()).getAmount()
                        * detail.getQuantity();

            // 할인 금액 적용
            if(Objects.nonNull(detail.getDiscountAmount()))
                amount -= detail.getDiscountAmount();

            totalAmount += amount;
        }
        return totalAmount;
    }

    // todo Payment 처리 로직 추가
    @Override
    public void userOrderProcess(OrderDTO order,
                                 List<OrderDetailDTO> orderDetailList,
                                 ReceiverInfoDTO receiverInfo,
                                 User user,
                                 OrderedPersonInfoDTO orderedPersonInfo){

        int totalAmount = getTotalAmountFromOrderDetailList(orderDetailList);
        Order newOrder = Order.builder()
                .deliveryFee(getDeliveryFee(totalAmount))
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

    @Override
    public void nonUserOrderProcess(OrderDTO order,
                                    List<OrderDetailDTO> orderDetailList,
                                    ReceiverInfoDTO receiverInfo,
                                    OrderedPersonInfoDTO orderedPersonInfo){

        int totalAmount = getTotalAmountFromOrderDetailList(orderDetailList);
        Order newOrder = Order.builder()
                .deliveryFee(getDeliveryFee(totalAmount))
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

    @Override
    public String getOrderUuid(){
        return UUID.randomUUID().toString();
    }

    @Override
    public void saveOrderInfoInRedisWithUuid(String uuid, OrderRequestDto orderInfo){
        orderRedisTemplate.opsForValue().set(uuid, orderInfo);
    }

    @Override
    public OrderResponseDto getOrderResponseFromOrderRequestDtoInRedis(String uuid){
        OrderResponseDto response = new OrderResponseDto();

        OrderRequestDto dto = orderRedisTemplate.opsForValue().get(uuid);

        // customerKey 생성
        String customerKey = UUID.randomUUID().toString().replace("-", "");

        // Amount 생성
        String currency = "KRW";

        /*
            OrderDetailList 를 통해서 다음 내용 ( value, orderName ) 생성함

            todo: OrderDetailDto마다 쿠폰 할인 금액 적용
             for-each : orderDetailList
                discountAmount = coupon 할인가
         */
        List<OrderDetailDTO> orderDetailList = Objects.requireNonNull(dto).getOrderDetailDTOList();

        int value = getTotalAmountFromOrderDetailList(orderDetailList);

        // OrderName 생성
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
}
