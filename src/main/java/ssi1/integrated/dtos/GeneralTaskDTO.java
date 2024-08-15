package ssi1.integrated.dtos;


import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.project_board.Status;

@Getter
@Setter
public class GeneralTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Status status;
}
