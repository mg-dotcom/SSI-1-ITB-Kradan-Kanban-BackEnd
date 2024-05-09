package ssi1.integrated.dtos;

import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    private String statusName;
}
