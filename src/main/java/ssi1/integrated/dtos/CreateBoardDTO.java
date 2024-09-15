package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardDTO {
    @NotNull
    @NotEmpty
    @NotBlank(message = "Board name is required")
    @Size(max = 120, message = "size must be between 0 and 120")
    private String name;
    @NotBlank(message = "Emoji is required")
    private String emoji;
    @NotBlank(message = "Color is required")
    private String color;
}
