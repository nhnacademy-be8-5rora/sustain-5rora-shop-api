package store.aurora.book.exception.category;

import store.aurora.common.exception.DataNotFoundException;

public class CategoryNotFoundException extends DataNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Long categoryId) {
        super("카테고리 ID " + categoryId + "를 찾을 수 없습니다.");
    }

}
