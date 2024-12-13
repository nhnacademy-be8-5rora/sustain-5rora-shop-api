package store.aurora.book.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.book.exception.BookNotFoundException;

import java.time.LocalDateTime;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler({
            BookNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFoundExceptions(RuntimeException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                e.getMessage(),
                404,
                LocalDateTime.now()
        );
        return ResponseEntity.status(404).body(errorResponseDto);
    }

}
