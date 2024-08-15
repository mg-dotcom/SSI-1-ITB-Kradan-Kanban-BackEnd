<<<<<<<< HEAD:src/main/java/ssi1/integrated/board/entities/Task.java
package ssi1.integrated.board.entities;
========
package ssi1.integrated.project_board;
>>>>>>>> pbi15-bi:src/main/java/ssi1/integrated/project_board/Task.java

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;


@Getter
@Setter
@ToString
@Entity
@Table(name = "task",schema = "integrated1")
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

    @ManyToOne
    @JoinColumn(name="statusId", nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(name="createdOn",  nullable = false, updatable = false ,insertable = false )
    private ZonedDateTime createdOn;
    @UpdateTimestamp
    @Column(name="updatedOn" ,nullable = false,insertable = false)
    private ZonedDateTime updatedOn;


}
