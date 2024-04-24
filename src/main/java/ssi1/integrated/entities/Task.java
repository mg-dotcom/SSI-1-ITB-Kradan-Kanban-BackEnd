package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    private Integer taskID;
    private String taskTitle;
    private String taskDescription;
    private String taskAssigned;
    private String taskStatus;
    private String createdOn;
    private String updatedOn;
}
