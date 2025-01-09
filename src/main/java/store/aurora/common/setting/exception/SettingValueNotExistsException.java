package store.aurora.common.setting.exception;

import store.aurora.common.exception.DataNotFoundException;

public class SettingValueNotExistsException extends DataNotFoundException {
    public SettingValueNotExistsException(String message) {
        super(message);
    }
}
