package ssi1.integrated.project_board.task_attachment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ssi1.integrated.project_board.task.Task;

import java.time.ZonedDateTime;

@Entity
@Table(name = "task_attachment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachmentId", nullable = false, unique = true)
    private Integer id;

    @Column(name = "fileName", nullable = false)
    private String fileName;

    // If you want to store the file content, rename to fileData
    @Lob // Large Object annotation for file content
    private byte[] fileData;

    @CreationTimestamp
    @Column(name = "createdOn", nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}

