package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.ShipmentInformation;
import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class ShipmentInformationRepositoryTest {
    @Autowired
    private ShipmentInformationRepository shipmentInformationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findByOrder() {
        // given
        Order order = new Order();
        order.setDeliveryFee(1000);
        order.setDeliveryFee(0);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(50000);
        order.setPointAmount(0);
        order.setState(OrderState.CONFIRMED);
        order.setName("John Doe");
        order.setOrderPhone("010-1234-5678");
        order.setOrderEmail("johndoe@example.com");
        order.setPreferredDeliveryDate(null); // 배송 희망 날짜 설정 안 함
        Order savedOrder = orderRepository.save(order);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrder(savedOrder); // Order와 매핑
        shipmentInformation.setReceiverName("Jane Doe");
        shipmentInformation.setReceiverAddress("123 Main Street, Seoul");
        shipmentInformation.setReceiverPhone("010-9876-5432");
        shipmentInformation.setCustomerRequest("문 앞에 놓아주세요.");

        shipmentInformationRepository.save(shipmentInformation);

        Optional<ShipmentInformation> foundShipmentInformation = shipmentInformationRepository.findById(savedOrder.getId());

        assertTrue(foundShipmentInformation.isPresent());
        assertEquals("Jane Doe", foundShipmentInformation.get().getReceiverName());
        assertEquals("123 Main Street, Seoul", foundShipmentInformation.get().getReceiverAddress());
        assertEquals("010-9876-5432", foundShipmentInformation.get().getReceiverPhone());
        assertEquals("문 앞에 놓아주세요.", foundShipmentInformation.get().getCustomerRequest());

        assertEquals(savedOrder.getId(), foundShipmentInformation.get().getOrder().getId());
    }
}