package ssi1.integrated.dtos;


import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.task_attachment.TaskFile;

import java.util.List;

@Getter
@Setter
public class GeneralTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Status status;
    private List<TaskFile> files;
}
