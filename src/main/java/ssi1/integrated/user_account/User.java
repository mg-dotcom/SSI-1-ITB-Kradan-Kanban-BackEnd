package ssi1.integrated.user_account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",schema = "user_account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String oid;
    private String name;
    private String username;
    private String email;
    private String password;
    @ColumnDefault("STUDENT")
    private String role;
}
