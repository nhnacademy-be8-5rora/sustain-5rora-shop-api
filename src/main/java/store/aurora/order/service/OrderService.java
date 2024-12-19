package store.aurora.order.service;

import store.aurora.order.entity.Order;

import java.util.List;

public interface OrderService {
    void createOrder(Order order);
    Order getOrder(Long orderId);
    List<Order> getOrders();
    void updateOrder(Order order);
    void deleteOrder(Order order);
}
