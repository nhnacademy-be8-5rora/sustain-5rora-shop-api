package store.aurora.order.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.exception.exception404.ShipmentNotFoundException;
import store.aurora.order.repository.ShipmentRepository;
import store.aurora.order.service.impl.ShipmentServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

    private ShipmentRepository shipmentRepository;
    private ShipmentService shipmentService;

    @BeforeEach
    void setUp() {
        shipmentRepository = Mockito.mock(ShipmentRepository.class);
        shipmentService = new ShipmentServiceImpl(shipmentRepository);
    }

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
        Mockito.when(shipmentRepository.existsById(999L)).thenReturn(false);

        assertFalse(shipmentService.isExist(999L));
    }

    @Test
    void createShipment() {
        Shipment shipment = new Shipment();
        shipment.setState(ShipmentState.PENDING);
        Mockito.when(shipmentRepository.save(shipment)).thenReturn(shipment);

        shipmentService.createShipment(shipment);

        Mockito.verify(shipmentRepository, Mockito.times(1)).save(shipment);
    }

    @Test
    void getShipment() {
        Shipment shipment1 = new Shipment();
        shipment1.setId(1L);
        shipment1.setState(ShipmentState.PENDING);
        Shipment shipment2 = new Shipment();
        shipment2.setId(2L);
        shipment2.setState(ShipmentState.PENDING);

        when(shipmentRepository.existsById(anyLong())).thenReturn(true);
        when(shipmentRepository.getReferenceById(1L)).thenReturn(shipment1);
        when(shipmentRepository.getReferenceById(2L)).thenReturn(shipment2);

        Assertions.assertAll(
                () -> assertEquals(shipment1, shipmentService.getShipment(1L)),
                () -> assertEquals(shipment2, shipmentService.getShipment(2L))
        );
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
        assertThrows(ShipmentNotFoundException.class, () -> {
            shipmentService.getShipment(999L);
        });
    }

    @Test
    void deleteByShipmentId() {
        when(shipmentRepository.existsById(1L)).thenReturn(true);

        shipmentService.deleteByShipmentId(1L);

        Mockito.verify(shipmentRepository, times(1)).deleteById(1L);
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
        assertThrows(ShipmentNotFoundException.class, () -> {
            shipmentService.deleteByShipmentId(999L);
        });
    }
}