package ssi1.integrated.project_board.collab_management;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.user_board.UserBoard;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "collab_management", schema = "integrated2")
public class CollabManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collabNo")
    private Integer collabNo;


    @JoinColumn(name = "user_oid", nullable = false)
    private String user_oid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_right", nullable = false)
    private AccessRight accessRight = AccessRight.READ;

    @Column(name = "addedOn", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime addedOn;
}
