package store.aurora.book.exception.category;

public class CategoryLimitException extends RuntimeException {
    public CategoryLimitException() {
        super("책에는 최소한 하나 이상의 카테고리가 연결되어 있어야 합니다.");
    }
}

