package store.aurora.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.repository.ShipmentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@Import(QuerydslConfiguration.class)
class ShipmentServiceTest {

    @MockBean
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentService shipmentService;

    @Test
    void isExist() {
        when(shipmentRepository.existsById(1L)).thenReturn(true);
        assertTrue(shipmentService.isExist(1L));
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
    void getShipment() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setState(ShipmentState.PENDING);

        when(shipmentRepository.existsById(1L)).thenReturn(true);
        when(shipmentRepository.getReferenceById(anyLong())).thenReturn(shipment);

        Long id = shipmentService.getShipment(1L).getId();
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
        when(shipmentRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.getShipment(999L);
        });
    }

    @Test
    void updateShipment() {
        Long id = 1L;
        Shipment shipment = new Shipment();
        shipment.setId(id);
        shipment.setState(ShipmentState.PENDING);

        when(shipmentRepository.existsById(1L)).thenReturn(true);
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        assertDoesNotThrow(() -> shipmentService.updateShipment(shipment));

        verify(shipmentRepository, times(1)).save(shipment);
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

        when(shipmentRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.updateShipment(shipment);
        });
    }

    @Test
    void updateShipmentWithNullState() {
        Shipment shipment = new Shipment();

        when(shipmentRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.updateShipment(shipment);
        });
    }

    @Test
    void deleteByShipmentId() {
        when(shipmentRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> shipmentService.deleteByShipmentId(1L));
    }

    @Test
    void deleteByShipmentIdWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.deleteByShipmentId(null);
        });
    }

    @Test
    void deleteByShipmentIdWithNonExistId() {
        when(shipmentRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.deleteByShipmentId(999L);
        });
    }
}