package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.service.BookService;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
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

    @Override
    public boolean isExist(Long orderDetailId) {
        if(Objects.isNull(orderDetailId)) {
            throw new IllegalArgumentException("OrderDetailId is null");
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
            throw new IllegalArgumentException("OrderDetailId is null");
        }
        if(!isExist(orderDetailId)) {
            throw new IllegalArgumentException("OrderDetail does not exist");
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
            throw new IllegalArgumentException("Order does not exist");
        }

        return orderDetailRepository.findByOrder(order);
    }

    @Override
    public void updateOrderDetail(OrderDetail orderDetail) {
        validate(orderDetail);

        if(!isExist(orderDetail.getId())) {
            throw new IllegalArgumentException("OrderDetail does not exist");
        }

        orderDetailRepository.save(orderDetail);
    }

    @Override
    public void deleteOrderDetailById(Long orderDetailId) {
        if(Objects.isNull(orderDetailId)) {
            throw new IllegalArgumentException("OrderDetailId is null");
        }
        if(!isExist(orderDetailId)) {
            throw new IllegalArgumentException("OrderDetail does not exist");
        }
        orderDetailRepository.deleteById(orderDetailId);
    }

    private void validate(OrderDetail orderDetail){
        if (Objects.isNull(orderDetail)) {
            throw new IllegalArgumentException("OrderDetail is null");
        }
        if(Objects.isNull(orderDetail.getOrder())) {
            throw new IllegalArgumentException("Order must not be null");
        }
        if(!orderService.isExist(orderDetail.getOrder().getId())) {
            throw new IllegalArgumentException("Order does not exist");
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
            throw new IllegalArgumentException("Shipment does not exist");
        }
        if(Objects.isNull(orderDetail.getAmountDetail())) {
            throw new IllegalArgumentException("AmountDetail must not be null");
        }
    }
}
