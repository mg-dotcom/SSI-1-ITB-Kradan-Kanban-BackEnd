package ssi1.integrated.dtos;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import ssi1.integrated.entities.TaskEnum;

import java.util.Date;
@Getter
@Setter

public class TaskDTO {
    private Integer id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskEnum status;
}
