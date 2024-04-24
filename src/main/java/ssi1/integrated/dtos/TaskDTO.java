package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Integer taskID;
    private String taskTitle;
    private String taskAssigned;
    private String taskStatus;
}
