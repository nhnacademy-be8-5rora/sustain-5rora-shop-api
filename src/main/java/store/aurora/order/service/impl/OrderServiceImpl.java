package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.order.entity.Order;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.service.OrderService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public boolean isExist(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId is null");
        }
        return orderRepository.existsById(orderId);
    }

    @Override
    public void createOrder(Order order) {
        // Order 유효성 검증
        String error = isValuable(order);
        if(!Objects.isNull(error)){
            throw new IllegalArgumentException(error);
        }

        orderRepository.save(order);
    }

    @Override
    public Order getOrder(Long orderId) {
        if(orderId == null){
            throw new IllegalArgumentException("orderId is null");
        }
        if(!isExist(orderId)){
            throw new IllegalArgumentException("주문 정보가 없음");
        }
        return orderRepository.getReferenceById(orderId);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void updateOrder(Order order) {
        // Order 유효성 검증
        if(!Objects.isNull(isValuable(order))){
            throw new IllegalArgumentException(isValuable(order));
        }

        // Order가 존재하는지 확인
        if(!isExist(order.getId())){
            throw new IllegalArgumentException("주문 정보가 없음");
        }

        orderRepository.save(order);
    }

    @Override
    public void deleteOrderById(Long orderId) {
        if(!isExist(orderId)){
            throw new IllegalArgumentException("주문 정보가 없음");
        }

        orderRepository.deleteById(orderId);
    }

    private String isValuable(Order order){
        if(order == null){
            return "order is null";
        }

        if(Objects.isNull(order.getDeliveryFee())){
            return "deliveryFee is null";
        }
        if(Objects.isNull(order.getOrderTime())){
            return "orderTime is null";
        }
        // default value를 설정했는데 필요할까?
        if(Objects.isNull(order.getTotalAmount())){
            return "totalAmount is null";
        }
        // default value를 설정했는데 필요할까?2
        if(Objects.isNull(order.getPointAmount())){
            return "pointAmount is null";
        }
        if(Objects.isNull(order.getState())){
            return "state is null";
        }
        if(Objects.isNull(order.getName())){
            return "name is null";
        }
        if(Objects.isNull(order.getOrderPhone())){
            return "orderPhone is null";
        }
        if(Objects.isNull(order.getPassword()) && Objects.isNull(order.getUser())){
            return "주문 유저에 대한 정보가 없음";
        }

        return null;
    }
}
