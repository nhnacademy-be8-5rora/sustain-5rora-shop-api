package store.aurora.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.admin.dto.AdminOrderDTO;
import store.aurora.order.admin.service.AdminDeliveryService;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.service.UserOrderInfoService;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminDeliveryService adminDeliveryService;
    private final UserOrderInfoService userOrderInfoService;
    private final SimpleEncryptor simpleEncryptor;

    @GetMapping
    public Page<AdminOrderDTO> getAllOrderList(@RequestParam("page") int page,
                                               @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        return adminDeliveryService.getAllOrderList(pageable);
    }

    @PostMapping
    public void updateShipmentStatus(@RequestParam("order-id") Long orderId,
                                     @RequestParam("shipment-state") String shipmentState){
        adminDeliveryService.updateShipmentStatusOfOrder(orderId, shipmentState);
    }

    @Operation(summary = "주문 상태로 주문조회", description = "주문상태를 입력받아 해당하는 주문들 조회")
    @Parameters(value = {
            @Parameter(name = "state", required = true, description = "주문 상태", example = "PENDING"),
            @Parameter(name = "page", description = "페이지. 0부터 시작"),
            @Parameter(name = "size", description = "크기")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/orders")
    public Page<OrderInfoDto> getAllOrdersByState(@RequestParam("state") String state,
                                                  Pageable pageable){
        OrderState orderState = OrderState.fromString(state);
        return userOrderInfoService.getOrderInfosByState(orderState, pageable);
    }

    @Operation(summary = "환불수락", description = "환불요청이 되어있는 주문을 수락")
    @Parameters(value = {
            @Parameter(name = "order-id", required = true, description = "환불요청되어있는 주문의 주문번호")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 주문 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/orders/{order-id}/refund")
    public Long resolveRefund(@PathVariable("order-id") String encryptedOrderId){
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
        return userOrderInfoService.resolveRefund(orderId);
    }

}
