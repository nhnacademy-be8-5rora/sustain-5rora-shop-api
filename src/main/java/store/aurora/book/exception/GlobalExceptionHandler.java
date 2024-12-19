package store.aurora.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import store.aurora.book.exception.category.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<String> handleInvalidCategoryException(InvalidCategoryException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400 상태 코드
                .body(ex.getMessage()); // 예외 메시지를 응답 본문에 포함
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 상태 코드
                .body("카테고리를 찾을 수 없습니다.");
    }

    @ExceptionHandler(SubCategoryExistsException.class)
    public ResponseEntity<String> handleSubCategoryExistsException(SubCategoryExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 상태 코드
                .body("하위 카테고리가 존재하여 삭제할 수 없습니다.");
    }

    @ExceptionHandler(CategoryLinkedToBooksException.class)
    public ResponseEntity<String> handleCategoryLinkedToBooksException(CategoryLinkedToBooksException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 상태 코드
                .body("이 카테고리는 책과 연결되어 있어 삭제할 수 없습니다.");
    }

    // 기타 예외 처리 추가 가능
}