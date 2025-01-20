package store.aurora.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.common.dto.ValidationErrorResponse;
import store.aurora.common.exception.*;
import store.aurora.file.ObjectStorageException;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import store.aurora.file.TokenRefreshException;
import store.aurora.key.KeyManagerJsonParsingException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger("user-logger");

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundExceptions(RuntimeException e) {
        return createResponseEntity(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleDataConflictExceptions(DataConflictException e) {
        return createResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({DataLimitExceededException.class, DataInsufficientException.class, ImageException.class,DtoNullException.class})
    public ResponseEntity<ErrorResponseDto> handleAlreadyException(RuntimeException e) {
        return createResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleEnumParseError(HttpMessageNotReadableException ex) {
        if (ex.getMessage().contains("Cannot deserialize value of type")) {
            ErrorResponseDto errorResponse = new ErrorResponseDto(
                    "Enum 파싱 에러: 요청 값이 Enum 타입에 맞지 않습니다." + ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return createResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectStorageException.class)
    public ResponseEntity<ErrorResponseDto> handleObjectStorageException(ObjectStorageException e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                e.getMessage(),
                e.getStatus().value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenRefreshException(TokenRefreshException e) {
        LOG.error("토큰 갱신 실패: {}", e.getMessage(), e);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    @ExceptionHandler(KeyManagerJsonParsingException.class)
    public ResponseEntity<ErrorResponseDto> handleKeyManagerJsonParsingException(KeyManagerJsonParsingException e) {
        LOG.error("JSON 파싱 오류: {}", e.getMessage(), e);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception e) {
        return createResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponseDto> createResponseEntity(Exception e, HttpStatus status) {
        LOG.error(e.getMessage(), e);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDto(
                        e.getMessage(),
                        status.value(),
                        LocalDateTime.now()
                ));
    }
}