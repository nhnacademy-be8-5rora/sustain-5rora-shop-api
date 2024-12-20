package store.aurora.order.mapper;

import store.aurora.order.entity.Order;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.user.entity.User;

import java.time.LocalDateTime;

public class OrderMapper {
    public static Order orderMapper(Integer deliveryFee, LocalDateTime orderTime,
                                    Integer totalAmount, Integer pointAmount,
                                    OrderState state,
                                    String name, String orderPhone,
                                    String password, User user) {

        Order o = new Order();


        o.setDeliveryFee(deliveryFee);
        o.setOrderTime(orderTime);
        o.setTotalAmount(totalAmount);
        o.setPointAmount(pointAmount);
        o.setState(state);
        o.setName(name);
        o.setOrderPhone(orderPhone);
        o.setPassword(password);
        o.setUser(user);
        return o;
    }
}
