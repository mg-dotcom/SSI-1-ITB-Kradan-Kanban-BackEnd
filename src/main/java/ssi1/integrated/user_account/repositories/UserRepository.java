package ssi1.integrated.user_account.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ssi1.integrated.user_account.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByOid(String ownerId);
}