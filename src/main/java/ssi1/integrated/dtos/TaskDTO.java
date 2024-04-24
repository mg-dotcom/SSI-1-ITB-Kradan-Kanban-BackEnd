package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter

public class TaskDTO {
    private Integer taskID;
    private String taskTitle;
    private String taskDescription;
    private String taskStatus;
}
