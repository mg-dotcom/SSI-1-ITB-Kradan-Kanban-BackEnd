package ssi1.integrated.user_account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByOid(String ownerId);

    Boolean existsByUsername(String username);
}