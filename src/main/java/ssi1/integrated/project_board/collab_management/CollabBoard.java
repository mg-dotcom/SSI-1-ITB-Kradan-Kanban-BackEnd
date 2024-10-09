package ssi1.integrated.project_board.collab_management;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.user_local.UserLocal;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "collab_management", schema = "integrated2")
public class CollabBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collabNo")
    private Integer collabNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_oid", nullable = false)
    private UserLocal user_oid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_right", nullable = false)
    private AccessRight accessRight = AccessRight.READ;

    @CreationTimestamp
    @Column(name = "addedOn",nullable = false)
    private ZonedDateTime addedOn;
}
