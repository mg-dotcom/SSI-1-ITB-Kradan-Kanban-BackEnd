package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NotNull
public class CreateBoardDTO {
    @NotNull
    @NotEmpty
    @NotBlank(message = "Board name is required")
    @Size(max = 120, message = "size must be between 0 and 120")
    private String name;
    private String emoji;
    private String color;
}
