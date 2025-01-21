package store.aurora.common.setting.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.common.setting.SettingTable;
import store.aurora.common.setting.exception.SettingValueNotExistsException;
import store.aurora.common.setting.repository.SettingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingServiceImplTest {
    @Mock private SettingRepository repo;

    @InjectMocks private SettingServiceImpl settingService;

    @Test
    void saveSetting() {
        settingService.saveSetting("key", "value");

        verify(repo).save(any());
    }

    @Test
    void saveSettingWithNullKey() {
        assertThrows(IllegalArgumentException.class, () -> settingService.saveSetting(null, "value"));
    }

    @Test
    void saveSettingWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> settingService.saveSetting("key", null));
    }

    @Test
    void getSettingValue() {
        // given
        SettingTable mock = new SettingTable();
        mock.setKey("key");
        mock.setValue("value");

        when(repo.findById("key")).thenReturn(Optional.of(mock));

        // when
        String result = settingService.getSettingValue("key");

        // then
        assertEquals("value", result);
    }

    @Test
    void getSettingValueWithNullKey() {
        assertThrows(IllegalArgumentException.class, () -> settingService.getSettingValue(null));
    }

    @Test
    void getSettingValueWithNullValue() {
        assertThrows(SettingValueNotExistsException.class, () -> settingService.getSettingValue("key"));
    }

    @Test
    void getDeliveryFee() {
        when(repo.findById(anyString())).thenReturn(Optional.of(new SettingTable("deliveryFee", "5000")));

        int result = settingService.getDeliveryFee();

        assertEquals(5000, result);
    }

    @Test
    void getDeliveryFeeWithNullValue() {
        when(repo.findById(anyString())).thenReturn(Optional.empty());

        int result = settingService.getDeliveryFee();

        assertEquals(5000, result);
    }

    @Test
    void getMinAmount() {
        when(repo.findById(anyString())).thenReturn(Optional.of(new SettingTable("minAmount", "30000")));

        int result = settingService.getMinAmount();

        assertEquals(30000, result);
    }

    @Test
    void getMinAmountWithNullValue() {
        when(repo.findById(anyString())).thenReturn(Optional.empty());

        int result = settingService.getMinAmount();

        assertEquals(30000, result);
    }
}