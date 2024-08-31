package ssi1.integrated.project_board.statusSetting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.repository.cdi.Eager;

@Getter
@Setter
@ToString
@Entity
@Table(name = "status_setting",schema = "integrated1")
public class StatusSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusSettingId")
    private Integer id;

    @Column
    private Boolean limitMaximumTask;

    @Column
    private Integer maximumTask;


}
