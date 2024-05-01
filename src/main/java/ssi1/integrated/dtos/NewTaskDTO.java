package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private String status;
}
