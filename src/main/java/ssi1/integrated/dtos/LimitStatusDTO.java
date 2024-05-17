package ssi1.integrated.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class LimitStatusDTO {
   @NotNull
   private Integer id;
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String name;
    @NotEmpty
    @Size(max = 200)
    private String description;
    private Boolean limitMaximumTask;
    private Integer noOfTasks;
     @JsonInclude(JsonInclude.Include.NON_NULL)
     // Ignore for test case
     @JsonIgnore
     private Integer maximumTask;
     @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TaskDTO> tasks;

    private String statusColor;

     public void setName(String title){
      this.name = title.trim();
     }

     public void setDescription(String description) {
      this.description = (description != null) ? description.trim() : null;
     }


     public void setStatusColor(String statusColor){
      this.statusColor = (statusColor != null) ? statusColor.trim() : "#CCCCCC";
     }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
        updateNoOfTasks();
     }

     private void updateNoOfTasks() {
         if (tasks != null) {
          this.noOfTasks = tasks.size();
         } else {
          this.noOfTasks = 0;
         }
     }

 }


