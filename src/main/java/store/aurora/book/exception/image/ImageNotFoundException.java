package store.aurora.book.exception.image;

import store.aurora.common.exception.DataNotFoundException;

public class ImageNotFoundException extends DataNotFoundException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}
