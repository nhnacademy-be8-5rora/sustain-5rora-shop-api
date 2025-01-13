package store.aurora.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.aurora.order.dto.OrderInfo;
import store.aurora.order.entity.Order;
import store.aurora.user.entity.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query(value = "select o.id as orderId," +
            "       o.total_amount as totalAmount," +
            "       o.state as orderState," +
            "       o.order_time as orderTime," +
            "       group_concat(b.title) as orderContent" +
            "    from orders o" +
            "        left join order_details od on o.id = od.order_id" +
            "        left join books b on od.book_id = b.id" +
            "        where user_id = :userId" +
            "        group by o.id, o.total_amount, o.state, o.order_time" +
            "    order by o.order_time desc" +
            "    limit :#{#pageable.pageSize}" +
            "    offset :#{#pageable.offset}",

            countQuery = "select count(distinct o.id)" +
                    "    from orders o" +
                    "    left join order_details od on o.id = od.order_id" +
                    "    where user_id = :userId",
            nativeQuery = true)
    Page<OrderInfo> findOrderInfosByUserId(@Param("userId")String userId, Pageable pageable);
}
