package store.aurora.order.controller;

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

    @GetMapping("/orders")
    public Page<OrderInfoDto> getAllOrdersByState(@RequestParam("state") String state,
                                                  Pageable pageable){
        OrderState orderState = OrderState.fromString(state);
        return userOrderInfoService.getOrderInfosByState(orderState, pageable);
    }

    @PostMapping("/orders/{order-id}/refund")
    public Long resolveRefund(@PathVariable("order-id") String encryptedOrderId){
        Long orderId = Long.parseLong(simpleEncryptor.decrypt(encryptedOrderId));
        return userOrderInfoService.resolveRefund(orderId);
    }

}
