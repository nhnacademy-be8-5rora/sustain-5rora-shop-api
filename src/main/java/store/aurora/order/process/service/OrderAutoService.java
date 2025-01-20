package store.aurora.order.process.service;

public interface OrderAutoService {
    int updateOrderAndOrderDetailsState(int daysThreshold);
}
