package ssi1.integrated.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatusDTO {
    private Integer id;
    private String name;
    private String description;
    private String statusColor;
    private List<TaskDTO> tasks;
}
