package store.aurora.order.process.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.order.process.service.impl.OrderAutoServiceImpl;
import store.aurora.order.repository.OrderRepository;

import static org.junit.jupiter.api.Assertions.*;

class OrderAutoServiceTest {

    private OrderAutoService orderAutoService;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderAutoService = new OrderAutoServiceImpl(orderRepository);
    }

    @Test
    void updateOrderAndOrderDetailStateTest() {
        Mockito.when(orderRepository.updateOrderAndDetailsForExpiredShipments(Mockito.anyInt())).thenReturn(1);

        int actual = orderAutoService.updateOrderAndOrderDetailsState(30);

        assertEquals(1, actual);
        Mockito.verify(orderRepository, Mockito.times(1)).updateOrderAndDetailsForExpiredShipments(Mockito.anyInt());
    }
}