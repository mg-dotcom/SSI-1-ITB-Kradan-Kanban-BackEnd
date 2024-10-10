package ssi1.integrated.project_board.collab_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollabBoardRepository extends JpaRepository<CollabBoard, Integer> {
    List<CollabBoard> findAllByBoardId(String boardId);
    @Query("SELECT CollabBoard FROM CollabBoard cb WHERE cb.board.id = :boardId AND cb.user_oid = :user_oid")
    CollabBoard findByUser_oidAndBoard_Id(String user_oid,String boardId);

//  Boolean existsByUser_oidAndBoard_Id(String user_oid,String boardId);
}
