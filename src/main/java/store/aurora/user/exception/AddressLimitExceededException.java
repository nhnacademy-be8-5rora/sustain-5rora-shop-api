package store.aurora.user.exception;

import store.aurora.common.exception.DataLimitExceededException;

public class AddressLimitExceededException extends DataLimitExceededException {
    public AddressLimitExceededException() {
        super("최대 10개의 주소만 등록할 수 있습니다. 기존 주소를 삭제한 후 다시 시도해 주세요.");
    }
}