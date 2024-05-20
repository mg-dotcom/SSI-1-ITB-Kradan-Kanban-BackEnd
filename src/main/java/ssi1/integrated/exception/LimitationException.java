package ssi1.integrated.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LimitationException extends ResponseStatusException {
    public LimitationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
