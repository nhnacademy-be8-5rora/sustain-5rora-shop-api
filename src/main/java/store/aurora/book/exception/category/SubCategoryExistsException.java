package store.aurora.book.exception.category;

public class SubCategoryExistsException extends RuntimeException {
  public SubCategoryExistsException(Long categoryId) {
    super("하위 카테고리가 있는 경우 삭제할 수 없습니다. 먼저 하위 카테고리를 삭제하세요. 카테고리 ID: " + categoryId);
  }
}
