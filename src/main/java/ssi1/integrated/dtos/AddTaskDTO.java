package ssi1.integrated.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddTaskDTO {
    @NotNull
    private Integer id;
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String title;
    @NotEmpty
    @Size(max = 500)
    private String description;
    @NotEmpty
    @Size(max = 30)
    private String assignees;
    private String status;
}
