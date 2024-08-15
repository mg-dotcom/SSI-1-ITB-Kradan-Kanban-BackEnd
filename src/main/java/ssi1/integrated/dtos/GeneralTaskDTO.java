package ssi1.integrated.dtos;


import lombok.Getter;
import lombok.Setter;
<<<<<<< HEAD
import ssi1.integrated.board.entities.Status;
=======
import ssi1.integrated.project_board.Status;
>>>>>>> pbi15-bi

@Getter
@Setter
public class GeneralTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Status status;
}
