package ssi1.integrated.dtos;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
    @NotNull
    private Boolean limitMaximumTask;

    public void setName(String title){
        this.name = title.trim();
    }

    public void setDescription(String description){
        this.description =  (description != null) ? description.trim() : description;
    }


}
