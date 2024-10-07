package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.board.Visibility;

@Getter
@Setter
public class BoardVisibilityDTO {
    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility = Visibility.PRIVATE;

    public void setVisibility(Visibility visibility) {
        this.visibility = (visibility == null) ? Visibility.PRIVATE : visibility;
    }
}
