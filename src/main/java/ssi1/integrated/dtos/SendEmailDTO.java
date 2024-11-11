package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDTO {
    private String to;
    private String subject;
    private String body;

}

