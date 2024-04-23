package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter

public class TaskDTO {
    private String taskTitle;
    private String taskDescription;
    private String taskStatus;
    private String taskAssigned;
    private Date createdOn;
    private Date updatedOn;
}
