package store.aurora.point.exception;

import store.aurora.common.exception.DataAlreadyExistsException;

public class PointPolicyAlreadyExistsException extends DataAlreadyExistsException {
    public PointPolicyAlreadyExistsException(String pointPolicy) {
        super("이미 존재하는 포인트 정책 아이디 또는 이름 입니다: " + pointPolicy);
    }
}