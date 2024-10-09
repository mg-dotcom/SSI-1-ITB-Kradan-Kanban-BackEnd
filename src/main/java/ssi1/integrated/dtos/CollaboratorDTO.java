package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.user_local.UserLocal;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CollaboratorDTO {
    private String user_oid;
    private String collaboratorName;
    private String collaboratorEmail;
    private AccessRight accessRight;
    private ZonedDateTime addedOn;
}
