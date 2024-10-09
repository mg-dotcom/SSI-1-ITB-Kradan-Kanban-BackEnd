package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.collab_management.AccessRight;

@Getter
@Setter
public class CollabBoardDTO {
    private String boardID;
    private String collaboratorName;
    private String collaboratorEmail;
    private AccessRight accessRight;
}