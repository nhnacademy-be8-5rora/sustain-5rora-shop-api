package store.aurora.order.service;

import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    void createOrderDetail(OrderDetail orderDetail);
    OrderDetail getOrderDetail(Long orderDetailId);
    List<OrderDetail> getOrderDetails();
    List<OrderDetail> getOrderDetailsByOrder(Order order);
    void updateOrderDetail(OrderDetail orderDetail);
    void deleteOrderDetail(OrderDetail orderDetail);
}
