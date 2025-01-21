package store.aurora.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.order.admin.dto.AdminOrderDTO;
import store.aurora.order.admin.service.AdminDeliveryService;
import store.aurora.order.dto.OrderInfoDto;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.service.UserOrderInfoService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminOrderControllerTest {

    private AdminOrderController adminOrderController;
    private AdminDeliveryService adminDeliveryService;
    private UserOrderInfoService userOrderInfoService;
    private final SimpleEncryptor simpleEncryptor = new SimpleEncryptor();

    @BeforeEach
    void setUp() {
        adminDeliveryService = Mockito.mock(AdminDeliveryService.class);
        userOrderInfoService = Mockito.mock(UserOrderInfoService.class);
        adminOrderController = new AdminOrderController(adminDeliveryService, userOrderInfoService, simpleEncryptor);
    }

    @Test
    void getAllOrderListTest() {
        PageImpl<AdminOrderDTO> expect = new PageImpl<>(List.of());
        Mockito.when(adminDeliveryService.getAllOrderList(Mockito.any(Pageable.class))).thenReturn(expect);

        assertEquals(expect, adminOrderController.getAllOrderList(0, 10));
    }

    @Test
    void updateShipmentStatus() {
        Mockito.doNothing().when(adminDeliveryService).updateShipmentStatusOfOrder(Mockito.anyLong(), Mockito.anyString());

        adminOrderController.updateShipmentStatus(1L, "PENDING");

        Mockito.verify(adminDeliveryService, Mockito.times(1)).updateShipmentStatusOfOrder(1L, "PENDING");
    }

    @Test
    void getAllOrdersByState() {
        PageImpl<OrderInfoDto> expect = new PageImpl<>(List.of());
        Mockito.when(userOrderInfoService.getOrderInfosByState(Mockito.any(OrderState.class), Mockito.any(Pageable.class))).thenReturn(expect);

        Page<OrderInfoDto> actual = adminOrderController.getAllOrdersByState("PENDING", PageRequest.of(0, 10));

        assertEquals(expect, actual);
    }

    @Test
    void resolveRefundTest() {
        Long orderId = 1L;
        String encryptedOrderId = simpleEncryptor.encrypt(String.valueOf(orderId));
        Mockito.when(userOrderInfoService.resolveRefund(1L)).thenReturn(orderId);

        Long actual = adminOrderController.resolveRefund(encryptedOrderId);

        assertEquals(orderId, actual);
    }
}