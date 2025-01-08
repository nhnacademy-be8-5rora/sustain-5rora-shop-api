package store.aurora.common.setting.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.common.setting.SettingTable;
import store.aurora.common.setting.repository.SettingRepository;
import store.aurora.common.setting.service.SettingService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository repo;

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
            throw new IllegalArgumentException("value is null");
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
}
