
package ssi1.integrated.project_board.task;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.status.Status;

import java.time.ZonedDateTime;


@Getter
@Setter
@ToString
@Entity
@Table(name = "task",schema = "integrated2")
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

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

}
