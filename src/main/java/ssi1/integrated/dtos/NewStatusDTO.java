package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.LimitationException;

@Getter
@Setter
public class NewStatusDTO {
    private Integer id;
    @NotNull
    @NotEmpty
    @Size(max = 50, message = "size must be between 0 and 50")
    private String name;
    @Size(max = 200, message = "size must be between 0 and 200")
    private String description;
    private String statusColor;

    public void setName(String name) {
        if (name == null ) {
            throw new BadRequestException("Name must not be null");
        } else if(name.isEmpty()){
            throw new BadRequestException("Name must not be empty");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : null;
    }

    public void setStatusColor(String statusColor){
        this.statusColor = (statusColor != null) ? statusColor.trim() : "#CCCCCC";
    }
}
