package store.aurora.order.process.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.common.setting.service.SettingService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceImplTest {

    @Mock
    SettingService settingService;

    @InjectMocks
    DeliveryFeeServiceImpl deliveryFeeService;

    @Test
    void getDeliveryFeeWhenAmountIsZero() {
        // given
        int totalAmount = -1;

        // when | then
        assertThatThrownBy(() -> deliveryFeeService.getDeliveryFee(totalAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getDeliveryFeeWhenAmountIsOverThenMinAmount() {
        // given
        when(settingService.getMinAmount()).thenReturn(30000);
        int totalAmount = 30000;

        // when
        int deliveryFee = deliveryFeeService.getDeliveryFee(totalAmount);

        // then
        assertThat(deliveryFee).isZero();
    }

    @Test
    void getDeliveryFeeWhenAmountIsUnderThenMinAmount() {
        // given
        when(settingService.getMinAmount()).thenReturn(30000);
        when(settingService.getDeliveryFee()).thenReturn(3000);
        int totalAmount = 20000;

        // when
        int deliveryFee = deliveryFeeService.getDeliveryFee(totalAmount);

        // then
        assertThat(deliveryFee).isEqualTo(3000);
    }
}