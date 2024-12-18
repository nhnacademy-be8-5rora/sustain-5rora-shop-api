package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class OrderDetailRepositoryTest {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findByOrder() {
        // Arrange
        Order order = new Order();
        order.setDeliveryFee(1000);
        order.setDeliveryFee(0);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(50000);
        order.setPointAmount(0);
        order.setState(OrderState.CONFIRMED);
        order.setName("John Doe");
        order.setOrderPhone("010-1234-5678");
        order.setOrderEmail("johndoe@example.com");
        orderRepository.save(order);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setState(OrderState.CONFIRMED);
        orderDetail.setAmountDetail(100);
        orderDetail.setAmountDetail(1);
        orderDetailRepository.save(orderDetail);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setOrder(order);
        orderDetail1.setState(OrderState.CONFIRMED);
        orderDetail1.setAmountDetail(100);
        orderDetail1.setAmountDetail(1);
        orderDetailRepository.save(orderDetail1);

        // Act
        List<OrderDetail> retrievedOrderDetails = orderDetailRepository.findByOrder(order);
        OrderDetail retrievedOrderDetail = retrievedOrderDetails.getFirst();

        // Assert
        assertEquals(2, retrievedOrderDetails.size());
        assertNotNull(retrievedOrderDetail);
        assertEquals(orderDetail.getId(), retrievedOrderDetail.getId());
    }
}