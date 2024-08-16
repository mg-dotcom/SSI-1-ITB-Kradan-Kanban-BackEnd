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
@Table(name = "users",schema = "itbkk_shared")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String oid;
    private String username;
    private String password;
    private String email;
    @ColumnDefault("STUDENT")
    private String role;
}
