package store.aurora.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public abstract class DataNotFoundException extends RuntimeException {
    protected DataNotFoundException(String message) {
        super(message);
    }
}
