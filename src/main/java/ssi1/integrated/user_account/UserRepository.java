package ssi1.integrated.user_account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);


    User findByOid(String ownerId);

    Boolean existsByUsername(String username);

    User findByEmail(String email);
}