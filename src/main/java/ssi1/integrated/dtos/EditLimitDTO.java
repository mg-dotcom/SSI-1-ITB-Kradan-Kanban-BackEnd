package ssi1.integrated.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditLimitDTO {
    private Boolean limitMaximumTask;

    public void setLimitMaximumTask(Boolean limitMaximumTask) {
        this.limitMaximumTask = (limitMaximumTask != null) ? limitMaximumTask : false;
    }
}
