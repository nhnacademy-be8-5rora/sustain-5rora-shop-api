package store.aurora.book.exception.tag;

import store.aurora.common.exception.DataNotFoundException;

public class TagNotFoundException extends DataNotFoundException {
    public TagNotFoundException(String message) {
        super(message);
    }
}
