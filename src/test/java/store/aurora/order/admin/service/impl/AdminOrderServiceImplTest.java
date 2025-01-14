package store.aurora.order.admin.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import store.aurora.order.service.OrderService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(value = {MockitoExtension.class})
class AdminOrderServiceImplTest {

    @Mock
    OrderService orderService;

    @InjectMocks
    AdminOrderServiceImpl adminOrderService;

    @Test
    void getAllOrderList() {
        adminOrderService.getAllOrderList(PageRequest.of(0, 1));

        verify(orderService, times(1)).getOrders();
    }
}