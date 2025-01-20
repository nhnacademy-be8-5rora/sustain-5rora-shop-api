package store.aurora.user.exception;

import store.aurora.common.exception.DataConflictException;

public class DuplicateUserException extends DataConflictException {
  public DuplicateUserException(String message) {
    super(message);
  }
}
