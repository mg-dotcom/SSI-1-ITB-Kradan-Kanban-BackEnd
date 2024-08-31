package ssi1.integrated.project_board.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ssi1.integrated.user_account.User;

@Getter
@Setter
@ToString
@Entity
@Table(name = "board",schema = "integrated1")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String user_oid;
}
