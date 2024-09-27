package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.board.Visibility;

@Getter
@Setter
@NotNull
public class CreateBoardDTO {
    @NotNull
    @NotEmpty
    @NotBlank(message = "Board name is required")
    @Size(max = 120, message = "size must be between 0 and 120")
    private String name;
    private String emoji = "📋";
    private String color = "#D3D3D3";
    @NotNull(message = "Visibility cannot be null")
    private Visibility visibility = Visibility.PRIVATE;

    public void setEmoji(String emoji) {
        this.emoji = (emoji == null) ? "📋" : emoji;
    }

    public void setColor(String color) {
        this.color = (color == null) ? "#DEDEDE" : color;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = (visibility == null) ? Visibility.PRIVATE : visibility;
    }
}
