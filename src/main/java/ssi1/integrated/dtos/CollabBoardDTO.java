package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollabBoardDTO {
    private String boardID;
    private String collaboratorName;
    private String collaboratorEmail;
    private Enum accessRight;
}
