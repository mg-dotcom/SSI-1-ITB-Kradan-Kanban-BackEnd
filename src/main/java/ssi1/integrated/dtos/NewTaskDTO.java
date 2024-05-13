package ssi1.integrated.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewTaskDTO {
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
    private Integer status;

    public void setTitle(String title){
        this.title = title.trim();
    }

    public void setDescription(String description){
        this.description =  (description != null) ? description.trim() : description;
    }

    public void setAssignees(String assignees){
        this.assignees = (assignees != null) ? assignees.trim() : assignees;
    }


}