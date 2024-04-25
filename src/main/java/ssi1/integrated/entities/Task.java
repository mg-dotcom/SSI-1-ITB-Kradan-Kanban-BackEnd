package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    @Enumerated(EnumType.STRING)
    private TaskEnum status;
    private Date createdOn;
    private Instant updatedOn;
}
