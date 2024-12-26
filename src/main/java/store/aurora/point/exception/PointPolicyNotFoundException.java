package store.aurora.point.exception;

import store.aurora.common.exception.DataNotFoundException;

public class PointPolicyNotFoundException extends DataNotFoundException {
    public PointPolicyNotFoundException(Integer id) {
        super("PointPolicy not found with id " + id);
    }
}