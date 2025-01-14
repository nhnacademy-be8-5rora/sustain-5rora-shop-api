package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderResponseDto;
import store.aurora.order.dto.OrderUuidAndRedirectUrlDto;
import store.aurora.order.process.dto.OrderCompleteRequestDto;
import store.aurora.order.process.service.OrderProcessService;

import java.util.Objects;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessService orderProcessService;

    // todo : 에러 처리 및 예외 처리 추가
    @PostMapping("/save-order-info")
    public OrderUuidAndRedirectUrlDto createOrderUuidAndResponseUuid(
            @RequestBody OrderRequestDto orderRequestDto
            ) {
        // uuid 생성
        String orderId = orderProcessService.getOrderUuid();

        // uuid를 키 값으로 레디스에 저장
        orderProcessService.saveOrderInfoInRedisWithUuid(orderId, orderRequestDto);

        // 생성한 uuid를 redirect url로 변환
        String redirectUrl = "/order/payment?order-id=" + orderId;

        //  uuid를 포함한 redirect url 반환
        return new OrderUuidAndRedirectUrlDto(redirectUrl);
    }

    @GetMapping("/{order-id}/get-order-info")
    public OrderResponseDto getOrderInfo(@PathVariable(name="order-id") String orderId){
        return orderProcessService.getOrderResponseFromOrderRequestDtoInRedis(orderId);
    }

    /* todo: ResponseDTO 추가
        비회원 주문 시 주문 id 값 넘겨주기 (적절한 값 찾아서)
     */
    @PostMapping("/order-complete")
    public void orderComplete(
            @RequestBody OrderCompleteRequestDto dto
            ){
        if(Objects.nonNull(dto.getIsGuest()) && Boolean.TRUE.equals(dto.getIsGuest()))
            orderProcessService.nonUserOrderProcess(dto.getOrderId(), dto.getPaymentKey(), dto.getAmount());
        else
            orderProcessService.userOrderProcess(dto.getOrderId(), dto.getPaymentKey(), dto.getAmount());
    }
}
