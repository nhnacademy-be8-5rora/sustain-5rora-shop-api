package store.aurora.common.setting.service;

public interface SettingService {
    void saveSetting(String key, String value);
    String getSettingValue(String key);

    int getDeliveryFee();
    int getMinAmount();
}
