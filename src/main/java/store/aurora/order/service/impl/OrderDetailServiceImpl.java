package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.service.book.BookService;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.exception.exception404.OrderDetailNotFoundException;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.exception.exception404.ShipmentNotFoundException;
import store.aurora.order.repository.OrderDetailRepository;
import store.aurora.order.service.OrderDetailService;
import store.aurora.order.service.OrderService;
import store.aurora.order.service.ShipmentService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderService orderService;
    private final OrderDetailRepository orderDetailRepository;
    private final ShipmentService shipmentService;
    private final BookService bookService;

    private static final String ID_ARGUMENT_IS_NULL = "OrderDetailId is null";

    @Override
    public boolean isExist(Long orderDetailId) {
        if(Objects.isNull(orderDetailId)) {
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        return orderDetailRepository.existsById(orderDetailId);
    }

    @Override
    public void createOrderDetail(OrderDetail orderDetail) {
        validate(orderDetail);
        orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long orderDetailId) {
        if(Objects.isNull(orderDetailId)) {
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        if(!isExist(orderDetailId)) {
            throw new OrderDetailNotFoundException(orderDetailId);
        }
        return orderDetailRepository.getReferenceById(orderDetailId);
    }

    @Override
    public List<OrderDetail> getOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrder(Order order) {
        if(Objects.isNull(order)) {
            throw new IllegalArgumentException("Order is null");
        }
        if(!orderService.isExist(order.getId())) {
            throw new OrderNotFoundException(order.getId());
        }

        return orderDetailRepository.findByOrder(order);
    }

    @Override
    public void updateOrderDetail(OrderDetail orderDetail) {
        validate(orderDetail);

        if(!isExist(orderDetail.getId())) {
            throw new OrderDetailNotFoundException(orderDetail.getId());
        }

        orderDetailRepository.save(orderDetail);
    }

    @Override
    public void deleteOrderDetailById(Long orderDetailId) {
        if(Objects.isNull(orderDetailId)) {
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        if(!isExist(orderDetailId)) {
            throw new OrderDetailNotFoundException(orderDetailId);
        }
        orderDetailRepository.deleteById(orderDetailId);
    }

    @Override
    public Integer getTotalWrapCostByOrder(Long orderId) {
        return orderDetailRepository.calculateTotalWrapCostByOrderId(orderId);
    }

    private void validate(OrderDetail orderDetail){
        if (Objects.isNull(orderDetail)) {
            throw new IllegalArgumentException(ID_ARGUMENT_IS_NULL);
        }
        if(Objects.isNull(orderDetail.getOrder())) {
            throw new IllegalArgumentException("Order must not be null");
        }
        if(!orderService.isExist(orderDetail.getOrder().getId())) {
            throw new OrderNotFoundException(orderDetail.getOrder().getId());
        }

        // Book Not Null
        if(Objects.isNull(orderDetail.getBook())) {
            throw new IllegalArgumentException("Book must not be null");
        }
        // Check if book exists
        else{
            bookService.notExistThrow(orderDetail.getBook().getId());
        }

        if(Objects.isNull(orderDetail.getQuantity())) {
            throw new IllegalArgumentException("Quantity must not be null");
        }
        if(Objects.isNull(orderDetail.getShipment())) {
            throw new IllegalArgumentException("Shipment must not be null");
        }
        if(!shipmentService.isExist(orderDetail.getShipment().getId())) {
            throw new ShipmentNotFoundException(orderDetail.getShipment().getId());
        }
        if(Objects.isNull(orderDetail.getAmountDetail())) {
            throw new IllegalArgumentException("AmountDetail must not be null");
        }
    }
}
