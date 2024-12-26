package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.entity.enums.ShippingCompaniesCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class ShipmentRepositoryTest {
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    void saveAndFindShipment(){
        Shipment shipment = new Shipment();
        shipment.setShipmentCompaniesCode(ShippingCompaniesCode.LOGEN);
        shipment.setShipmentDatetime(LocalDateTime.now());
        shipment.setState(ShipmentState.PENDING);
        Shipment savedShipment = shipmentRepository.save(shipment);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setState(OrderState.CONFIRMED);
        orderDetail1.setAmountDetail(10000);
        orderDetail1.setQuantity(1);
        savedShipment.addOrderDetail(orderDetail1);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setState(OrderState.CONFIRMED);
        orderDetail2.setAmountDetail(20000);
        orderDetail2.setQuantity(2);
        savedShipment.addOrderDetail(orderDetail2);

        shipmentRepository.save(savedShipment);

        Optional<Shipment> foundShipment = shipmentRepository.findById(savedShipment.getId());

        assertTrue(foundShipment.isPresent());
        assertEquals(2, foundShipment.get().getOrderDetails().size());

        List<OrderDetail> orderDetails = foundShipment.get().getOrderDetails();
        assertEquals(10000, orderDetails.get(0).getAmountDetail());
        assertEquals(20000, orderDetails.get(1).getAmountDetail());
    }


}