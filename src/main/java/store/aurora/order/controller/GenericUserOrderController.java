package store.aurora.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "주문번호의 소유자검증", description = "회원번호 혹은 비밀번호로 주문번호의 소유 검증")
    @Parameters(value = {
            @Parameter(name = "order-id", required = true, description = "암호화된 주문번호"),
            @Parameter(name = "user-id", description = "암호화된 회원번호. 비회원의 경우 null"),
            @Parameter(name = "encryptedPassword", description = "암호화된 비밀번호. 회원의 경우 null")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검증 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(summary = "주문 취소", description = "상품이 배송되기 전 고객의 주문 취소")
    @Parameters(value = {
            @Parameter(name = "order-id", required = true, description = "암호화된 주문번호")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 주문 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/cancel")
    public Long orderCancel(@RequestParam("order-id") String encryptedOrderId){
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
        return userOrderInfoService.cancelOrder(orderId);
    }


    @Operation(summary = "환불 요청", description = "고객의 환불 요청")
    @Parameters(value = {
            @Parameter(name = "order-id", required = true, description = "암호화된 주문번호")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "환불 요청 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 주문 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/refund")
    public Long requestRefund(@RequestParam("order-id") String encryptedOrderId){
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
        return userOrderInfoService.requestRefund(orderId);
    }

    @Operation(summary = "비회원 주문 조회", description = "비회원의 주문 상세 정보 조회")
    @Parameters(value = {
            @Parameter(name = "code", required = true, description = "\"주문번호:비밀번호\" 형식의 문자열을 암호화한 값")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 주문 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
