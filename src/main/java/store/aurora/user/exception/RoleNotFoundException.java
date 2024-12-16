package store.aurora.user.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String userId) {
        super(String.format("Role not found for user %s", userId));
    }
}