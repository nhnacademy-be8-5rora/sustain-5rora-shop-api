package store.aurora.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.ShipmentState;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Import(QuerydslConfiguration.class)
class ShipmentServiceImplTest {
    @Autowired
    private ShipmentServiceImpl shipmentService;

    @Test
    @Sql("shipmentTest.sql")
    void isExist() {
        Long id = shipmentService.getShipments().get(0).getId();
        assertTrue(shipmentService.isExist(id));
    }

    @Test
    void isExistWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.isExist(null);
        });
    }

    @Test
    void isExistWithNonExistId() {
        assertFalse(shipmentService.isExist(999L));
    }

    @Test
    void createShipment() {
        Shipment shipment = new Shipment();
        shipment.setState(ShipmentState.PENDING);

        assertDoesNotThrow(() -> {
            shipmentService.createShipment(shipment);
        });
    }

    @Test
    @Sql("shipmentTest.sql")
    void getShipment() {
        Long id = shipmentService.getShipments().get(0).getId();
        assertDoesNotThrow(() -> {
            shipmentService.getShipment(id);
        });
    }

    @Test
    void getShipmentWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.getShipment(null);
        });
    }

    @Test
    void getShipmentWithNonExistId() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.getShipment(999L);
        });
    }

    @Test
    @Sql("shipmentTest.sql")
    void updateShipment() {
        Long id = shipmentService.getShipments().get(0).getId();
        Shipment shipment = shipmentService.getShipment(id);
        shipment.setState(ShipmentState.SHIPPED);

        assertAll(
                () -> assertDoesNotThrow(() -> shipmentService.updateShipment(shipment)),
                () -> assertEquals(ShipmentState.SHIPPED, shipmentService.getShipment(id).getState())
        );
    }

    @Test
    void updateShipmentWithNullShipment() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.updateShipment(null);
        });
    }

    @Test
    void updateShipmentWithNonExistId() {
        Shipment shipment = new Shipment();
        shipment.setId(999L);
        shipment.setState(ShipmentState.SHIPPED);

        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.updateShipment(shipment);
        });
    }

    @Test
    void updateShipmentWithNullState() {
        Shipment shipment = new Shipment();
        shipment.setState(null);

        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.updateShipment(shipment);
        });
    }

    @Test
    @Sql("shipmentTest.sql")
    void deleteByShipmentId() {
        assertAll(
                () -> assertDoesNotThrow(() -> shipmentService.deleteByShipmentId(1L)),
                () -> assertEquals(1, shipmentService.getShipments().size())
        );
    }

    @Test
    void deleteByShipmentIdWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.deleteByShipmentId(null);
        });
    }

    @Test
    void deleteByShipmentIdWithNonExistId() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.deleteByShipmentId(999L);
        });
    }
}