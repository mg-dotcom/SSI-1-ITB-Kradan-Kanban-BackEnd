package ssi1.integrated.project_board.user_local;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ssi1.integrated.project_board.collab_management.CollabBoard;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user", schema = "integrated2")
public class UserLocal {

    @Id
    @Column(name = "oid", nullable = false, unique = true, length = 36)
    private String oid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<CollabBoard> collabManagements = new ArrayList<>();

}
