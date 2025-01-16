package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.service.UserOrderInfoService;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class GenericUserOrderController {

    private final SimpleEncryptor simpleEncryptor;
    private final UserOrderInfoService userOrderInfoService;

    @GetMapping("/verify")
    public Boolean isOwner(@RequestParam("order-id") String encryptedOrderId,
                           @RequestParam(value = "user-id", required = false) String encryptedUserId, //비회원의 경우 null
                           @RequestParam(value = "encryptedPassword", required = false) String encryptedPassword){ //회원의 경우 null
        String userId;
        Long orderId;
        String password;
        try{
            userId = Objects.nonNull(encryptedUserId) ? simpleEncryptor.decrypt(encryptedUserId) : null;
            orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
            password = Objects.nonNull(encryptedPassword) ? simpleEncryptor.decrypt(encryptedPassword) : null;
        }catch (RuntimeException e){
            return false;
        }

        return userOrderInfoService.isOwner(orderId, userId, password);
    }

    @PostMapping("/cancel")
    public Long orderCancel(@RequestParam("order-id") String encryptedOrderId){
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
        return userOrderInfoService.cancelOrder(orderId);
    }

    @PostMapping("/refund")
    public Long requestRefund(@RequestParam("order-id") String encryptedOrderId){
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
        return userOrderInfoService.requestRefund(orderId);
    }
}
