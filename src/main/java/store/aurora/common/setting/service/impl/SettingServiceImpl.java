package store.aurora.common.setting.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.common.setting.SettingTable;
import store.aurora.common.setting.exception.SettingValueNotExistsException;
import store.aurora.common.setting.repository.SettingRepository;
import store.aurora.common.setting.service.SettingService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository repo;

    private static final String DEFAULT_DELIVERY_FEE_VALUE = "5000";
    private static final String DEFAULT_MIN_AMOUNT_VALUE = "30000";

    @Override
    public SettingTable saveSetting(String key, String value) {
        if(Objects.isNull(key) || Objects.isNull(value)){
            throw new IllegalArgumentException("key or value is null");
        }

        SettingTable setting = new SettingTable();
        setting.setKey(key);
        setting.setValue(value);
        return repo.save(setting);
    }

    @Override
    public String getSettingValue(String key) {
        if(Objects.isNull(key)){
            throw new IllegalArgumentException("key is null");
        }

        String value = repo.getReferenceById(key).getValue();
        if(Objects.isNull(value)){
            throw new SettingValueNotExistsException("value is null" + "\nkey: " + key);
        }
        return value;
    }

    @Override
    public void deleteSettingValue(String key) {
        if(Objects.isNull(key)){
            throw new IllegalArgumentException("key is null");
        }
        repo.deleteById(key);
    }

    @Override
    public int getDeliveryFee(){
        try{
            String deliveryFee = getSettingValue("deliveryFee");

            return Integer.parseInt(deliveryFee);
        } catch(SettingValueNotExistsException e){
            saveSetting("deliveryFee", DEFAULT_DELIVERY_FEE_VALUE);
            return Integer.parseInt(DEFAULT_DELIVERY_FEE_VALUE);
        }
    }

    @Override
    public int getMinAmount(){
        try{
            String minAmount = getSettingValue("minAmount");

            return Integer.parseInt(minAmount);
        } catch(SettingValueNotExistsException e){
            saveSetting("minAmount", DEFAULT_MIN_AMOUNT_VALUE);
            return Integer.parseInt(DEFAULT_MIN_AMOUNT_VALUE);
        }
    }
}
