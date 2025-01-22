package store.aurora.order.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.dto.OrderUuidAndRedirectUrlDto;
import store.aurora.order.entity.Order;
import store.aurora.order.process.dto.OrderCompleteRequestDto;
import store.aurora.order.process.service.OrderProcessService;
import store.aurora.point.service.PointHistoryService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {

    private OrderProcessService orderProcessService;
    private PointHistoryService pointHistoryService;
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderProcessService = Mockito.mock(OrderProcessService.class);
        pointHistoryService = Mockito.mock(PointHistoryService.class);
        orderController = new OrderController(orderProcessService, pointHistoryService);
    }

    @Test
    void createOrderUuidAndResponseUuidTest() {
        String uuid = UUID.randomUUID().toString();
        String expect = "/order/payment?order-id=" + uuid;
        Mockito.when(orderProcessService.getOrderUuid()).thenReturn(uuid);
        Mockito.doNothing().when(orderProcessService).saveOrderInfoInRedisWithUuid(Mockito.anyString(), Mockito.any());

        OrderUuidAndRedirectUrlDto actual = orderController.createOrderUuidAndResponseUuid(new OrderRequestDto());

        assertEquals(expect, actual.getRedirectUrl());
    }

    @Test
    void orderCompleteTest1() {
        Mockito.when(orderProcessService.nonUserOrderProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(1L);

        OrderCompleteRequestDto dto = new OrderCompleteRequestDto();
        dto.setIsGuest(true);
        dto.setOrderId("test");
        dto.setPaymentKey("test");
        dto.setAmount(1000);

        Long actual = orderController.orderComplete(dto);

        assertEquals(1L, actual);
    }

    @Test
    void orderCompleteTest2() {
        Order order = new Order();
        order.setId(1L);

        Mockito.when(orderProcessService.userOrderProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(order);
        Mockito.doNothing().when(pointHistoryService).earnOrderPoints(Mockito.any());

        OrderCompleteRequestDto dto = new OrderCompleteRequestDto();
        dto.setIsGuest(false);
        dto.setOrderId("test");
        dto.setPaymentKey("test");
        dto.setAmount(1000);

        Long actual = orderController.orderComplete(dto);

        assertEquals(order.getId(), actual);
    }

}