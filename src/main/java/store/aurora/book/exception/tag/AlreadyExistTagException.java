package store.aurora.book.exception.tag;

public class AlreadyExistTagException extends RuntimeException {
    public AlreadyExistTagException(Long tagId) {
        super(String.format("%d is already exist tag", tagId));
    }
}
