package ssi1.integrated.project_board.task_attachment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ssi1.integrated.project_board.task.Task;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "task_attachment", schema = "integrated2")
public class TaskFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachmentId", unique = true)
    private Integer id;


    @Column(name = "fileName", nullable = false)
    private String fileName;

    @Column(name = "fileSize", nullable = false)
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}

