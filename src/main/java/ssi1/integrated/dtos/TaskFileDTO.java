package ssi1.integrated.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.task_attachment.TaskFile;

import java.time.ZonedDateTime;

@Getter
@Setter
@Data
public class TaskFileDTO {
    private Integer id;
    private String fileName;
    private Long fileSize; // Consider adding a utility to convert size to KB/MB if needed
    private ZonedDateTime createdOn;
    private Integer taskId;
    private String boardId;

    public TaskFileDTO(TaskFile taskFile) {
        this.id = taskFile.getId();
        this.fileName = taskFile.getFileName();
        this.fileSize = taskFile.getFileSize();
        this.createdOn = taskFile.getCreatedOn();
        this.taskId = taskFile.getTask().getId();
        this.boardId = taskFile.getTask().getBoard().getId();
    }
}