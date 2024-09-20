
package ssi1.integrated.project_board.status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import ssi1.integrated.project_board.status.Status;

import java.util.List;


@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    boolean existsByName(String name);
    List<Status> findByBoardId(String boardId);
}


