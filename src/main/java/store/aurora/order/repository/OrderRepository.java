package store.aurora.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.aurora.order.dto.OrderDetailInfoDto;
import store.aurora.order.dto.OrderInfo;
import store.aurora.order.dto.OrderRelatedInfoWithAuth;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.user.entity.User;

import java.util.List;
import java.util.Optional;

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

    @Query(value = "select o.id as orderId," +
            "       o.total_amount as totalAmount," +
            "       o.state as orderState," +
            "       o.order_time as orderTime," +
            "       group_concat(b.title) as orderContent" +
            "    from orders o" +
            "        left join order_details od on o.id = od.order_id" +
            "        left join books b on od.book_id = b.id" +
            "        where o.state = :state" +
            "        group by o.id, o.total_amount, o.state, o.order_time" +
            "    order by o.order_time desc" +
            "    limit :#{#pageable.pageSize}" +
            "    offset :#{#pageable.offset}",

            countQuery = "select count(o.id)" +
                    "    from orders o" +
                    "    where o.state = :state",
            nativeQuery = true)
    Page<OrderInfo> findOrderInfosByOrderState(@Param("state")int stateOrdinal, Pageable pageable);

    @Query(value = "select new store.aurora.order.dto.OrderRelatedInfoWithAuth(" +
            "o.id, o.preferredDeliveryDate, o.deliveryFee, o.orderTime, o.totalAmount, " +
            "o.pointAmount, o.state, o.password, o.user.id, o.orderPhone, o.orderEmail, " +
            "o.shipmentInformation.receiverName, o.shipmentInformation.receiverPhone, o.shipmentInformation.receiverAddress, o.shipmentInformation.customerRequest) " +
            "from Order o " +
            "where o.id = :orderId")
    @EntityGraph(attributePaths = {"shipmentInformation", "user"})
    Optional<OrderRelatedInfoWithAuth> findOrderRelatedInfoByOrderId(Long orderId);

    @Query(value = "select od from OrderDetail od left join fetch od.order left join fetch od.book left join fetch od.shipment left join fetch od.wrap where od.order.id = :orderId order by od.id")
    List<OrderDetail> findOrderDetailByOrderId(@Param("orderId") Long orderId);

    @Query(value = "select o from Order o left join fetch o.user where o.id = :orderId")
    Optional<Order> findOrderWithUserByOrderId(Long orderId);

    @Query(value = "select o from Order o join fetch o.shipmentInformation join fetch o.payments join fetch o.user where o.id = :orderId")
    Optional<Order> findOrderByOrderIdWithShipmentInformationAndPaymentsAndUser(Long orderId);

    @Query(value =
            "update orders o " +
            "    left join order_details od on o.id = od.order_id " +
            "    left join shipments s on od.shipment_id = s.id " +
            "set o.state = 6, od.state = 6 " +
            "where s.shipment_datetime <= now() - interval :threshold day and o.state = 3", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    int updateOrderAndDetailsForExpiredShipments(@Param("threshold") int daysThreshold);
}
