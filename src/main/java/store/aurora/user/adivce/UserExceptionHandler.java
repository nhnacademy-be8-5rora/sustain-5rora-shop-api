package store.aurora.user.adivce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.aurora.user.exception.AlreadyActiveUserException;
import store.aurora.user.exception.DuplicateUserException;
import store.aurora.user.exception.VerificationException;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<Map<String, String>> handleVerificationException(VerificationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({DuplicateUserException.class, AlreadyActiveUserException.class})
    public ResponseEntity<Map<String, String>> handleConflictException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
    
}
