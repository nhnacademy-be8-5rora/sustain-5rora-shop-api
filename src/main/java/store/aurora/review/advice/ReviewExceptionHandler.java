package store.aurora.review.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.aurora.review.exception.UnauthorizedReviewException;

@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(UnauthorizedReviewException.class)
    public ResponseEntity<String> handleUnauthorizeReviewException(UnauthorizedReviewException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
