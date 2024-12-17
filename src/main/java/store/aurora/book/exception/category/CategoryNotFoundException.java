package store.aurora.book.exception.category;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Long categoryId) {
        super("카테고리 ID " + categoryId + "를 찾을 수 없습니다.");
    }

}
