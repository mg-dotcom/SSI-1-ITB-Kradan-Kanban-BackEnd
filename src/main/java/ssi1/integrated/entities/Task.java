package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date createdOn;
    private Date updatedOn;
}
