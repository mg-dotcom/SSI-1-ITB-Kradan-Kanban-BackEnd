package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.collab_management.AccessRight;

@Getter
@Setter
public class AddCollabBoardDTO {
    @NotNull(message = "Email must not be null")
    private String email;
    @NotNull(message = "Access right must not be null")
    private AccessRight accessRight;

}
