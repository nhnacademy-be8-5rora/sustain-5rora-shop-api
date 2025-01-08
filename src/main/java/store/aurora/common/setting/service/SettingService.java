package store.aurora.common.setting.service;

import store.aurora.common.setting.SettingTable;

public interface SettingService {
    SettingTable saveSetting(String key, String value);
    String getSettingValue(String key);
    void deleteSettingValue(String key);

    int getDeliveryFee();
    int getMinAmount();
}
