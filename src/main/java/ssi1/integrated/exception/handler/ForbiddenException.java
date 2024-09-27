package ssi1.integrated.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // Automatically returns 403 status
public class ForbiddenException extends RuntimeException {
    // Constructor without message
}