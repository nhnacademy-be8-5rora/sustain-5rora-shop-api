package store.aurora.book.exception.book;

public class ISBNAlreadyExistsException extends RuntimeException {
    public ISBNAlreadyExistsException(String isbn) {
        super(String.format("이미 등록된 ISBN: %s", isbn));
    }
}
