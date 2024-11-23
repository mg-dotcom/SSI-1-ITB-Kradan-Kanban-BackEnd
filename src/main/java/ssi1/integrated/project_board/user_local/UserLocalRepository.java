package ssi1.integrated.project_board.user_local;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserLocalRepository extends JpaRepository<UserLocal, String> {
    UserLocal findByOid(String userOid);
}
