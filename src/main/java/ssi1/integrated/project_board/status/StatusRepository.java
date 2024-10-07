package ssi1.integrated.project_board.status;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    boolean existsByName(String name);

    List<Status> findByBoardId(String boardId, Sort sort);

    List<Status> findByBoardId(String boardId);

    // Method to delete all statuses by boardId
    void deleteByBoardId(String boardId);
}


