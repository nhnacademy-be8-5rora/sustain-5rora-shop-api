package store.aurora.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.ShipmentInformation;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.exception.exception404.ShipmentInformationNotFoundException;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.repository.ShipmentInformationRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ShipmentInformationServiceTest {

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    ShipmentInformationRepository repo;

    @Autowired
    ShipmentInformationService service;

    @BeforeEach
    void setUp(){
        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setOrderTime(LocalDateTime.now());
        mockOrder.setName("");
        mockOrder.setPassword("");
        mockOrder.setOrderPhone("");
        mockOrder.setDeliveryFee(0);
        mockOrder.setTotalAmount(0);
        mockOrder.setPointAmount(0);
        mockOrder.setState(OrderState.PENDING);

        when(orderRepository.getReferenceById(anyLong())).thenReturn(mockOrder);
        when(orderRepository.existsById(mockOrder.getId()
        )).thenReturn(true);
    }

    @Test
    void createShipmentInformation() {
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId());
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        service.createShipmentInformation(shipmentInformation);

        verify(repo, times(1)).save(shipmentInformation);
    }
    @Test
    void createShipmentInformationWithNullOrderId(){
        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(null);
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        assertThrows(IllegalArgumentException.class, () -> {
            service.createShipmentInformation(shipmentInformation);
        });
    }
    @Test
    void createShipmentInformationWithNonExistOrderId(){
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId() + 1);
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        assertThrows(OrderNotFoundException.class, () -> {
            service.createShipmentInformation(shipmentInformation);
        });
    }
    @Test
    void createShipmentInformationWithNullColumns(){
        Order order = orderRepository.getReferenceById(anyLong());

        assertAll(
                () -> {
                    ShipmentInformation info = new ShipmentInformation();
                    info.setOrderId(order.getId());
                    info.setReceiverAddress(null);
                    info.setReceiverName("");
                    info.setReceiverPhone("");

                    assertThrows(IllegalArgumentException.class, () -> {
                        service.createShipmentInformation(info);
                    });
                },
                () -> {
                    ShipmentInformation info = new ShipmentInformation();
                    info.setOrderId(order.getId());
                    info.setReceiverAddress("");
                    info.setReceiverName(null);
                    info.setReceiverPhone("");

                    assertThrows(IllegalArgumentException.class, () -> {
                        service.createShipmentInformation(info);
                    });
                },
                () -> {
                    ShipmentInformation info = new ShipmentInformation();
                    info.setOrderId(order.getId());
                    info.setReceiverAddress("");
                    info.setReceiverName("");
                    info.setReceiverPhone(null);

                    assertThrows(IllegalArgumentException.class, () -> {
                        service.createShipmentInformation(info);
                    });
                }
        );
    }

    @Test
    void getShipmentInformation() {
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId());
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        when(repo.getReferenceById(anyLong())).thenReturn(shipmentInformation);
        when(repo.existsById(anyLong())).thenReturn(true);
        assertEquals(shipmentInformation, service.getShipmentInformation(1L));
    }
    @Test
    void getShipmentInformationWithNullOrderId(){
        assertThrows(IllegalArgumentException.class, () -> {
            service.getShipmentInformation(null);
        });
    }
    @Test
    void getShipmentInformationWithNonExistOrderId(){
        assertThrows(OrderNotFoundException.class, () -> {
            service.getShipmentInformation(2L);
        });
    }
    @Test
    void getShipmentInformationWithNonExistShipmentInformation(){
        assertThrows(ShipmentInformationNotFoundException.class, () -> {
            service.getShipmentInformation(1L);
        });
    }

    @Test
    void updateShipmentInformation() {
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId());
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        service.createShipmentInformation(shipmentInformation);

        shipmentInformation.setReceiverAddress("new address");
        shipmentInformation.setReceiverName("new name");
        shipmentInformation.setReceiverPhone("new phone");

        when(repo.existsById(anyLong())).thenReturn(true);
        service.updateShipmentInformation(shipmentInformation);

        verify(repo, times(2)).save(shipmentInformation);
    }
    @Test
    void updateShipmentInformationWithNullOrderId(){
        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(null);
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateShipmentInformation(shipmentInformation);
        });
    }
    @Test
    void updateShipmentInformationWithNonExistOrderId(){
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId() + 1);
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        assertThrows(OrderNotFoundException.class, () -> {
            service.updateShipmentInformation(shipmentInformation);
        });
    }
    @Test
    void updateShipmentInformationWithNonExistShipmentInformation(){
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId());
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        assertThrows(ShipmentInformationNotFoundException.class, () -> {
            service.updateShipmentInformation(shipmentInformation);
        });
    }
    @Test
    void updateShipmentInformationWithNullColumns(){
        Order order = orderRepository.getReferenceById(1L);

        ShipmentInformation shipmentInformation = new ShipmentInformation();
        shipmentInformation.setOrderId(order.getId());
        shipmentInformation.setReceiverAddress("");
        shipmentInformation.setReceiverName("");
        shipmentInformation.setReceiverPhone("");

        service.createShipmentInformation(shipmentInformation);

        when(repo.existsById(shipmentInformation.getOrderId())).thenReturn(true);
        assertAll(
                () -> {
                    shipmentInformation.setReceiverAddress(null);
                    shipmentInformation.setReceiverName("");
                    shipmentInformation.setReceiverPhone("");

                    assertThrows(IllegalArgumentException.class, () -> {
                        service.updateShipmentInformation(shipmentInformation);
                    });
                },
                () -> {
                    shipmentInformation.setReceiverAddress("");
                    shipmentInformation.setReceiverName(null);
                    shipmentInformation.setReceiverPhone("");

                    assertThrows(IllegalArgumentException.class, () -> {
                        service.updateShipmentInformation(shipmentInformation);
                    });
                },
                () -> {
                    shipmentInformation.setReceiverAddress("");
                    shipmentInformation.setReceiverName("");
                    shipmentInformation.setReceiverPhone(null);

                    assertThrows(IllegalArgumentException.class, () -> {
                        service.updateShipmentInformation(shipmentInformation);
                    });
                }
        );
    }

    @Test
    void deleteShipmentInformationById() {
        Order order = orderRepository.getReferenceById(1L);

        when(repo.existsById(order.getId())).thenReturn(true);

        service.deleteShipmentInformationById(1L);

        verify(repo, times(1)).deleteById(1L);
    }
    @Test
    void deleteShipmentInformationByIdWithNullOrderId(){
        assertThrows(IllegalArgumentException.class, () -> {
            service.deleteShipmentInformationById(null);
        });
    }
    @Test
    void deleteShipmentInformationByIdWithNonExistOrderId(){
        assertThrows(OrderNotFoundException.class, () -> {
            service.deleteShipmentInformationById(2L);
        });
    }
    @Test
    void deleteShipmentInformationByIdWithNonExistShipmentInformation(){
        when(repo.existsById(anyLong())).thenReturn(false);

        assertThrows(ShipmentInformationNotFoundException.class, () -> {
            service.deleteShipmentInformationById(1L);
        });
    }
}