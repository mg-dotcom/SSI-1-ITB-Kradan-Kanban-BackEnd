package ssi1.integrated.user_account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String name);

    Optional<User> findByOid(String ownerId);
}
