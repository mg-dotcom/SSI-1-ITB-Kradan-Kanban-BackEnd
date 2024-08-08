package ssi1.integrated.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {
    public ItemNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
