package store.aurora.user.exception;

import store.aurora.common.exception.DataNotFoundException;

public class RankNotFoundException extends DataNotFoundException {
  public RankNotFoundException(String userId) {
    super(String.format("Rank not found for user %s", userId));
  }
}
