package store.aurora.order.admin.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import store.aurora.book.entity.Book;
import store.aurora.order.admin.dto.AdminOrderDTO;
import store.aurora.order.admin.service.DeliveryStatusChanger;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.service.OrderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDeliveryServiceImplTest {

    @Mock
    OrderService orderService;

    @Mock
    DeliveryStatusChanger deliveryStatusChanger;

    @InjectMocks
    AdminDeliveryServiceImpl adminDeliveryService;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        Book mockBook = new Book();
        mockBook.setSalePrice(15000);

        Shipment mockShipment = Shipment.builder().shipmentDatetime(LocalDateTime.now()).build();
        OrderDetail mockOrderDetail = OrderDetail.builder().shipment(mockShipment).amountDetail(15000).quantity(2).book(mockBook).build();

        mockOrder = Order.builder()
                .id(1L)
                .state(OrderState.PENDING)
                .preferredDeliveryDate(LocalDate.now().plusDays(2))
                .orderDetails(List.of(mockOrderDetail))
                .build();
    }

    @Test
    void getAllOrderList_WithOrders_ShouldReturnPagedOrders() {
        // given
        when(orderService.getOrders()).thenReturn(List.of(mockOrder));

        // when
        Page<AdminOrderDTO> result = adminDeliveryService.getAllOrderList(PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getOrderId()).isEqualTo(mockOrder.getId());
        verify(orderService, times(1)).getOrders();
    }

    @Test
    void getAllOrderList_WithNoOrders_ShouldReturnEmptyPage() {
        // given
        when(orderService.getOrders()).thenReturn(Collections.emptyList());

        // when
        Page<AdminOrderDTO> result = adminDeliveryService.getAllOrderList(PageRequest.of(0, 1));

        // then
        assertThat(result.getTotalElements()).isZero();
        verify(orderService, times(1)).getOrders();
    }

    @Test
    void updateShipmentStatusOfOrderStatusIsSHIPPING() {
        // when
        adminDeliveryService.updateShipmentStatusOfOrder(1L, "SHIPPING");

        // then
        verify(deliveryStatusChanger, times(1)).updateOrderStatusToShipping(1L);
    }

    @Test
    void updateShipmentStatusOfOrderStatusIsPENDING() {
        // when
        adminDeliveryService.updateShipmentStatusOfOrder(1L, "PENDING");

        // then
        verify(deliveryStatusChanger, times(1)).updateOrderStatusToPending(1L);
    }

    @Test
    void updateShipmentStatusOfOrder_WithInvalidStatus_ShouldThrowException() {
        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> adminDeliveryService.updateShipmentStatusOfOrder(1L, "INVALID_STATUS"));
    }
}
