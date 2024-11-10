package ssi1.integrated.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.task_attachment.TaskFile;

import java.time.ZonedDateTime;
import java.util.Base64;

@Getter
@Setter
@Data
public class TaskFileDTO {
    private Integer id;
    private String fileName;
    private Long fileSize;
    private ZonedDateTime createdOn;
    private Integer taskId;
    private String boardId;
    private byte[] fileData;
    private String contentType;

    public TaskFileDTO(TaskFile taskFile) {
        this.id = taskFile.getId();
        this.fileName = taskFile.getFileName();
        this.fileSize = taskFile.getFileSize();
        this.createdOn = taskFile.getCreatedOn();
        this.taskId = taskFile.getTask().getId();
        this.boardId = taskFile.getTask().getBoard().getId();
        this.fileData = taskFile.getFileData();
        this.contentType = taskFile.getContentType();
    }

}