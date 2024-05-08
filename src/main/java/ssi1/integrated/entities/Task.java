package ssi1.integrated.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "tasks_v2")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="taskId", nullable = false ,unique = true)
    private Integer id;
    @Column(name="taskTitle")
    private String title;
    @Column(name="taskDescription")
    private String description;
    @Column(name="taskAssignees")
    private String assignees;
    //connect with status
    @ManyToOne
    @JoinColumn(name = "statusId")
    private Status status;
    @CreationTimestamp
    @Column(name="createdOn",  nullable = false, updatable = false ,insertable = false )
    private ZonedDateTime createdOn;
    @UpdateTimestamp
    @Column(name="updatedOn" ,nullable = false,insertable = false)
    private ZonedDateTime updatedOn;
}
