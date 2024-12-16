package store.aurora.book.exception.category;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long categoryId) {
        super("존재하지 않는 카테고리 ID입니다: " + categoryId);
    }
}
