package ssi1.integrated.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditLimitDTO {
    private Boolean limitMaximumTask;
    private Integer maximumTask;

    public void setLimitMaximumTask(Boolean limitMaximumTask) {
        this.limitMaximumTask = (limitMaximumTask != null) ? limitMaximumTask : true;
    }

    public void setMaximumTask(Integer maximumTask) {
        if (limitMaximumTask != null && limitMaximumTask) {
            this.maximumTask = (maximumTask != null) ? maximumTask : 10;
        } else {
            this.maximumTask = null;
        }
    }
}
