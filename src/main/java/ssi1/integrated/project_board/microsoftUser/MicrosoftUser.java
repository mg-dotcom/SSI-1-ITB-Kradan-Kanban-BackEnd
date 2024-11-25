package ssi1.integrated.project_board.microsoftUser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MicrosoftUser {
    private String id;
    private String givenName;
    private String displayName;
    private String mail;
}
