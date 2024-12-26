package store.aurora.user.exception;

import store.aurora.common.exception.DataNotFoundException;

public class RoleNotFoundException extends DataNotFoundException {
    public RoleNotFoundException(String userId) {
        super(String.format("Role not found for user %s", userId));
    }
}