package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "status_v2")
public class Status {
    @Id
    private Integer statusId;
    private String statusName;
    private String statusDescription;
    private String statusColor;

    //connect with task
    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private Set<Task> tasks=new LinkedHashSet<>();
}
