package ssi1.integrated.project_board.task_attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Path;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ssi1.integrated.project_board.task.Task;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "task") // Exclude the task from the toString to prevent potential circular reference issues
@Entity
@Table(name = "task_attachment", schema = "integrated2")
public class TaskFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachmentId", unique = true, nullable = false, updatable = false)
    private Integer id;

    @Column(name = "fileName", nullable = false, length = 255)
    private String fileName;

    @Column(name = "fileSize", nullable = false)
    private Long fileSize;

    @Column(name = "filePath", nullable = false, length = 500)
    private String filePath;

    @Column(name = "contentType", nullable = false, length = 100)
    private String contentType;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "taskId", nullable = false)
    @JsonIgnore
    private Task task;
}
