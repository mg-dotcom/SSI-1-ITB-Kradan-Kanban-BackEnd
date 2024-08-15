package ssi1.integrated.project_board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "status",schema = "integrated1")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
    private Integer id;
    @Column(name = "statusName",unique = true,nullable = false)
    private String name;
    @Column(name = "statusDescription")
    private String description;
    private String statusColor;


    @CreationTimestamp
    @Column(name="createdOn",  nullable = false, updatable = false ,insertable = false )
    private ZonedDateTime createdOn;
    @UpdateTimestamp
    @Column(name="updatedOn" ,nullable = false,insertable = false)
    private ZonedDateTime updatedOn;

    @JsonIgnore
    @OneToMany(mappedBy="status")
    private List<Task> tasks;
}