package store.aurora.review.exception;

import store.aurora.common.exception.DataNotFoundException;

public class ReviewNotFoundException extends DataNotFoundException {
    public ReviewNotFoundException(Long reviewId) {
        super(String.format("Review:%s not found", reviewId));
    }
}
