package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.user_account.UserDTO;

@Getter
@Setter
public class BoardDTO {
    private String id;
    @NotNull
    @NotEmpty
    @Size(max = 120, message = "size must be between 0 and 120")
    private String name;
    private UserDTO owner;
}
