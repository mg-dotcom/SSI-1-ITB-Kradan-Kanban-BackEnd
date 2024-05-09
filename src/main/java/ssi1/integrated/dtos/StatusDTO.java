package ssi1.integrated.dtos;

import lombok.Data;

import java.util.List;


@Data
public class StatusDTO {
    private Integer id;
    private String name;
    private String description;

    private String statusColor;
    private List<TaskDTO> tasks;

}
