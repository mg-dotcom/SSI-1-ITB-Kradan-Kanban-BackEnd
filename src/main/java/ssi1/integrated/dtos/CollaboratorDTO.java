package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.Status;
import ssi1.integrated.project_board.user_local.UserLocal;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CollaboratorDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight accessRight;
    private Status status;
    private ZonedDateTime addedOn;
}
