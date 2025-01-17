package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.dto.OrderWithOrderDetailResponse;
import store.aurora.order.service.UserOrderInfoService;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class GenericUserOrderController {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

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

    @GetMapping("/non-member/orders/{code}")
    public OrderWithOrderDetailResponse getNonMemberOrderInfo(@PathVariable("code") String code){
        String decrypted = simpleEncryptor.decrypt(code);
        log.info("decrypted code:{}", decrypted);

        if(!decrypted.matches("\\d{1,19}:.{1,255}")){
            throw new IllegalArgumentException(String.format("code(%s)는 잘못된 형식입니다.", decrypted));
        }
        String[] orderIdAndPassword = decrypted.split(":");

        long orderId = Long.parseLong(orderIdAndPassword[0]);
        String password = orderIdAndPassword[1];

        return userOrderInfoService.getOrderDetailInfos(orderId, null, password);
    }
}
