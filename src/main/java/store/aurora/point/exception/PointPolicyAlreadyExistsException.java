package store.aurora.point.exception;

import store.aurora.common.exception.DataAlreadyExistsException;

public class PointPolicyAlreadyExistsException extends DataAlreadyExistsException {
    public PointPolicyAlreadyExistsException(String pointPolicyName) {
        super("이미 존재하는 포인트 정책 이름입니다: " + pointPolicyName);
    }
}