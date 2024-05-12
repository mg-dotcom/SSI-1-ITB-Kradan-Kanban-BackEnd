package ssi1.integrated.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;


@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse{
    private final ZonedDateTime timestamp = ZonedDateTime.now();
    private final int status;
    private final String message;
    private final String instance;
}