package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.service.UserOrderInfoService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{user-id}/orders")
public class UserOrderInfoController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserOrderInfoService userOrderInfoService;
    private final SimpleEncryptor simpleEncryptor;

    @GetMapping
    public Page<OrderInfoDto> getOrderInfoList(@PathVariable("user-id") String encryptedId, Pageable pageable){
        return userOrderInfoService.getOrderInfos(simpleEncryptor.decrypt(encryptedId), pageable);
    }

    @GetMapping("{order-id}")
    public OrderWithOrderDetailResponse getOrderWithOrderDetail(@PathVariable("user-id") String encryptedUserId,
                                                                @PathVariable("order-id") String encryptedOrderId){
        String userId = simpleEncryptor.decrypt(encryptedUserId);
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));

        return userOrderInfoService.getOrderDetailInfos(orderId, userId, null);
    }
}
