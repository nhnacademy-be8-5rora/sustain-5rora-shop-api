package store.aurora.point.exception;

import store.aurora.common.exception.DataConflictException;

public class PointPolicyAlreadyExistsException extends DataConflictException {
    public PointPolicyAlreadyExistsException(String pointPolicy) {
        super("이미 존재하는 포인트 정책 아이디 또는 이름 입니다: " + pointPolicy);
    }
}