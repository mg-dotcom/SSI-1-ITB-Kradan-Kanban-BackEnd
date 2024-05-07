package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class NewTaskDTO {
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
    @NotNull
    @NotEmpty
    private String status;

    public void setTitle(String title){
        this.title = title.trim();
    }

    public void setDescription(String description){
        this.description = description.trim();
    }

    public void setAssignees(String assignees){
        this.assignees = assignees.trim();
    }

    public void setStatus(String status){
        this.status = (status == null || status.isEmpty()) ? "No Status" : status;
    }
}