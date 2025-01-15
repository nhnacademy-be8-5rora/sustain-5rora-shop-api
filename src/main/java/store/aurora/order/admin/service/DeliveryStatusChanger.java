package store.aurora.order.admin.service;

public interface DeliveryStatusChanger {
    void updateOrderStatusToShipping(Long orderid);
    void updateOrderStatusToPending(Long orderId);
    void scheduleOrderCompletion();
    void completeOrder(Long orderId);
}
