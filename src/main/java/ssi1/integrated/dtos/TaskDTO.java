package ssi1.integrated.dtos;

import lombok.Data;


@Data
public class TaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    private String statusName;
}
