package store.aurora.order.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.order.admin.service.DeliveryStatusChanger;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShippingStatusListenerTest {

    @Mock
    DeliveryStatusChanger deliveryStatusChanger;

    @InjectMocks
    ShippingStatusListener shippingStatusListener;

    @Test
    void receiveMessage() {
        shippingStatusListener.receiveMessage(1L);

        verify(deliveryStatusChanger).completeOrder(1L);
    }
}