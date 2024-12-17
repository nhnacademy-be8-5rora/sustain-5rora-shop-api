package store.aurora.book.exception.category;

public class CategoryLinkedToBooksException extends RuntimeException {
    public CategoryLinkedToBooksException(Long categoryId) {
        super("카테고리가 책과 연결되어 있어 삭제할 수 없습니다. 카테고리 ID: " + categoryId);
    }
}
