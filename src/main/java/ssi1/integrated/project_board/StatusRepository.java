
package ssi1.integrated.project_board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.project_board.Status;


@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    boolean existsByName(String name);
}


