package ssi1.integrated.project_board.collab_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollabManagementRepository extends JpaRepository<CollabManagement, Integer> {
    List<CollabManagement> findAllByBoardId(String boardId);

    CollabManagement findByUserOid(String userOid);
}
