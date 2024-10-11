package ssi1.integrated.project_board.collab_management;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "collaborator", schema = "integrated2")
public class CollabBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collabNo")
    private Integer collabNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(name = "accessRight", nullable = false)
    private AccessRight accessRight = AccessRight.READ;

    @CreationTimestamp
    @Column(name = "addedOn",nullable = false)
    private ZonedDateTime addedOn;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userOid", nullable = false)
    private UserLocal user;

}
