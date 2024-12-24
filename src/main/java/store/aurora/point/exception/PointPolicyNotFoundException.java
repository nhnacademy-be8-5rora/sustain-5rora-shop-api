package store.aurora.point.exception;

public class PointPolicyNotFoundException extends RuntimeException {
    public PointPolicyNotFoundException(Integer id) {
        super("PointPolicy not found with id " + id);
    }
}