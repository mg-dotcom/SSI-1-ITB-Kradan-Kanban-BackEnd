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
    private Integer id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskEnum status;
    private String assignees;
    private Date createdOn;
    private Date updatedOn;
}
