package ssi1.integrated.dtos;

import lombok.Data;




@Data

public class NewStatusDTO {
    private Integer id;
    private String name;
    private String description;

    private String statusColor;
}
