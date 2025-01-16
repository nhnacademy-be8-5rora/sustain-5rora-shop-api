package store.aurora.point.exception;

import store.aurora.common.exception.DataInsufficientException;

public class PointInsufficientException extends DataInsufficientException {
    public PointInsufficientException(Integer pointToSpend, Integer availablePoints) {
        super(String.format("Point %d is out of available points %d.", pointToSpend, availablePoints));
    }
}