package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.user_account.UserDTO;

@Getter
@Setter
public class BoardDTO {
    private String id;
    @NotNull
    @NotEmpty
    @Size(max = 120, message = "size must be between 0 and 120")
    private String name;
    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility;
    private UserDTO owner;

    public void setVisibility(Visibility visibility) {
        this.visibility = (visibility == null) ? Visibility.PRIVATE : visibility;
    }
}
