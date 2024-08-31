
package ssi1.integrated.project_board.status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import ssi1.integrated.project_board.status.Status;


@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    boolean existsByName(String name);
}


