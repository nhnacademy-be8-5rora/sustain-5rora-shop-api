package store.aurora.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.common.exception.DataAlreadyExistsException;
import store.aurora.common.exception.DataNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundExceptions(RuntimeException e) {
        return createResponseEntity(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyException(Exception e) {
        return createResponseEntity(e, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ErrorResponseDto> createResponseEntity(Exception e, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDto(
                        e.getMessage(),
                        status.value(),
                        LocalDateTime.now()
                ));
    }
}
