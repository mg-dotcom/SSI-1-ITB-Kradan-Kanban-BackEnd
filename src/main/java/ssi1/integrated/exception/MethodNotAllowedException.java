package ssi1.integrated.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MethodNotAllowedException extends ResponseStatusException {
    public MethodNotAllowedException(String message) {
        super(HttpStatus.METHOD_NOT_ALLOWED, message);
    }
}
