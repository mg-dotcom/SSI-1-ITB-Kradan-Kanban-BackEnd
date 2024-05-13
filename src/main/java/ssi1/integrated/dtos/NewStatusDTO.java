package ssi1.integrated.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class NewStatusDTO {
    @NotNull
    private Integer id;
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String name;
    @NotEmpty
    @Size(max = 200)
    private String description;
    private String statusColor;

<<<<<<< HEAD

    public void setName(String title){
        this.name = title.trim();
    }

    public void setDescription(String description){
        this.description =  (description != null) ? description.trim() : description;
    }

    public void setStatusColor(String statusColor){
        this.statusColor = (statusColor != null) ? statusColor.trim() : statusColor;
    }
}
=======
    public void setName(String name){
        this.name = name.trim();
    }

    public void setDescription(String description){
        this.description =  (description != null) ? description.trim() : null;
    }

    public void setStatusColor(String statusColor){
        this.statusColor = (statusColor != null) ? statusColor.trim() : "#CCCCC";
    }
}
>>>>>>> 84e22339caba39484031051a26d0cde2485e0397
