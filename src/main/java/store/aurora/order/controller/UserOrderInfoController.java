package store.aurora.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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

    private final UserOrderInfoService userOrderInfoService;
    private final SimpleEncryptor simpleEncryptor;

    @Operation(summary = "회원의 주문목록", description = "회원의 주문 목록 조회")
    @Parameters(value = {
            @Parameter(name = "user-id", required = true, description = "암호화된 회원번호"),
            @Parameter(name = "page", description = "페이지. 0부터 시작"),
            @Parameter(name = "size", description = "크기")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public Page<OrderInfoDto> getOrderInfoList(@PathVariable("user-id") String encryptedId, Pageable pageable){
        return userOrderInfoService.getOrderInfos(simpleEncryptor.decrypt(encryptedId), pageable);
    }

    @Operation(summary = "회원의 주문상세", description = "회원의 주문 상세 조회")
    @Parameters(value = {
            @Parameter(name = "user-id", required = true, description = "암호회된 회원번호"),
            @Parameter(name = "order-id", required = true, description = "암호화된 주문번호")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 주문 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("{order-id}")
    public OrderWithOrderDetailResponse getOrderWithOrderDetail(@PathVariable("user-id") String encryptedUserId,
                                                                @PathVariable("order-id") String encryptedOrderId){
        String userId = simpleEncryptor.decrypt(encryptedUserId);
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));

        return userOrderInfoService.getOrderDetailInfos(orderId, userId, null);
    }
}
