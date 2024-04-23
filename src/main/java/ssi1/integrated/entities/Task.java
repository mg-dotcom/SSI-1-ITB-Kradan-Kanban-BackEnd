package ssi1.integrated.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.enums.TaskStatus;


import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {
    @Id
    private Integer taskID;
    private String taskTitle;
    private String taskDescription;
    private TaskStatus taskStatus;
    private String taskAssigned;
    private Date createdOn;
    private Date updatedOn;

}
