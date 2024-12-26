package store.aurora.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public abstract class DataAlreadyExistsException extends RuntimeException {
    protected DataAlreadyExistsException(String message) {
        super(message);
    }
}
