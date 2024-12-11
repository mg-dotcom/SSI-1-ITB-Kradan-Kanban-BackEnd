package ssi1.integrated.project_board.collab_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollabBoardRepository extends JpaRepository<CollabBoard, Integer> {
    List<CollabBoard> findAllByBoardId(String boardId);
    CollabBoard findByBoard_IdAndUser_Oid(String boardId, String oid);
    List<CollabBoard> findByUser_OidOrderByAddedOnAsc(String userOid);
}