package store.aurora.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.book.exception.BookNotFoundException;
import store.aurora.point.exception.PointPolicyNotFoundException;
import store.aurora.user.exception.RoleNotFoundException;

import java.time.LocalDateTime;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler({
            BookNotFoundException.class,
            PointPolicyNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFoundExceptions(RuntimeException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                e.getMessage(),
                404,
                LocalDateTime.now()
        );
        return ResponseEntity.status(404).body(errorResponseDto);
    }

    // 전역 예외 처리
    @ExceptionHandler({
            RoleNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                e.getMessage(),
                500,
                LocalDateTime.now()
        );
        return ResponseEntity.status(500).body(errorResponseDto);
    }
}
