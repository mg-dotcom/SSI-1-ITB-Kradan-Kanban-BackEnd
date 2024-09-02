package ssi1.integrated.dtos;

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
    @Size(max = 120, message = "size must be between 0 and 120")
    private String name;
}
