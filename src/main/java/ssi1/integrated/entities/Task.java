package ssi1.integrated.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



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
    @Enumerated(EnumType.STRING)
    private TaskEnum taskStatus;
    private String taskAssigned;
    private Date createdOn;
    private Date updatedOn;
}
