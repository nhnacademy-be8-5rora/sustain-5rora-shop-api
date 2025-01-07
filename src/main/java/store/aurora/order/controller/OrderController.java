package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.order.dto.OrderUuidAndRedirectUrlDto;
import store.aurora.order.service.process.OrderProcessService;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessService orderProcessService;

    @PostMapping("/save-order-info")
    public OrderUuidAndRedirectUrlDto saveOrderInfo() {
        String orderUuid = orderProcessService.getOrderUuid();
        String redirectUrl = "/order/payment?order-id=" + orderUuid;
        return new OrderUuidAndRedirectUrlDto(redirectUrl);
    }
}
