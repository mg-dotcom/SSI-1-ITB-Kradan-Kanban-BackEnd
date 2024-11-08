package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ssi1.integrated.project_board.task_attachment.TaskFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewTaskDTO {
    private Integer id;

    @NotNull(message = "Task title must not be null")
    @NotEmpty(message = "Task title must not be empty")
    @Size(max = 100, message = "size must be between 0 and 100")
    private String title;

    @Size(max = 500, message = "size must be between 0 and 500")
    private String description;

    @Size(max = 30, message = "size must be between 0 and 30")
    private String assignees;

    private Integer status;
    private String statusName;

    private List<TaskFile> files;

    public void setTitle(String title) {
        if (title != null) {
            this.title = title.trim();
        } else {
            this.title = null;
        }
    }

    public void setDescription(String description) {
        String trimmedDescription = (description != null) ? description.trim() : null;
        this.description = (trimmedDescription != null && !trimmedDescription.isEmpty()) ? trimmedDescription : null;
    }

    public void setAssignees(String assignees) {
        String trimmedAssignees = (assignees != null) ? assignees.trim() : null;
        this.assignees = (trimmedAssignees != null && !trimmedAssignees.isEmpty()) ? trimmedAssignees : null;
    }

    public void setStatus(Integer statusId) {
        this.status = (statusId != null) ? statusId : 1;
    }

    public void setFiles(List<TaskFile> files) {
        this.files = (files != null) ? files : new ArrayList<>();
    }
}