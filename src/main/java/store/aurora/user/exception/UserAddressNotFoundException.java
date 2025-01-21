package store.aurora.user.exception;

import store.aurora.common.exception.DataConflictException;
import store.aurora.common.exception.DataNotFoundException;

public class UserAddressNotFoundException extends DataNotFoundException {
    public UserAddressNotFoundException(Long id) {
        super(String.format("해당 사용자에게 유저 주소 아이디 %d는 존재하지 않습니다.", id));
    }
}
