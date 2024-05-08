package ssi1.integrated.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ssi1.integrated.entities.Status;


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
    private Status status;


    public void setTitle(String title){
        this.title = title.trim();
    }

    public void setDescription(String description){
        this.description =  (description != null) ? description.trim() : description;
    }

    public void setAssignees(String assignees){
        this.assignees = (assignees != null) ? assignees.trim() : assignees;
    }

    public void setStatus(Status status) {
        this.status.setName(status == null ? "NO_STATUS" : status.getName());
    }

}