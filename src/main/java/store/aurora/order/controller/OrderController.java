package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderResponseDto;
import store.aurora.order.dto.OrderUuidAndRedirectUrlDto;
import store.aurora.order.service.process.OrderProcessService;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessService orderProcessService;

    @PostMapping("/create-uuid")
    public OrderUuidAndRedirectUrlDto createOrderUuidAndResponseUuid(
            @RequestBody OrderRequestDto orderRequestDto
            ) {
        // uuid 생성
        String orderUuid = orderProcessService.getOrderUuid();

        // uuid를 키 값으로 레디스에 저장
        orderProcessService.saveOrderInfoInRedisWithUuid(orderUuid, orderRequestDto);

        // 생성한 uuid를 redirect url로 변환
        String redirectUrl = "/order/payment?order-id=" + orderUuid;

        //  uuid를 포함한 redirect url 반환
        return new OrderUuidAndRedirectUrlDto(redirectUrl);
    }

    @GetMapping("/{uuid}/get-order-info")
    public OrderResponseDto getOrderInfo(@PathVariable String uuid){
        return orderProcessService.getOrderResponseFromOrderRequestDtoInRedis(uuid);
    }
}
