package ssi1.integrated.project_board.collab_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ssi1.integrated.project_board.user_local.UserLocal;

import java.util.List;

@Repository
public interface CollabBoardRepository extends JpaRepository<CollabBoard, Integer> {
    List<CollabBoard> findAllByBoardId(String boardId);
//    @Query("SELECT CollabBoard FROM CollabBoard cb WHERE cb.board.id = :boardId AND cb.user_oid.oid = :user_oid")
    CollabBoard findByBoard_IdAndUser_Oid(String boardId, String oid);

//  Boolean existsByUser_oidAndBoard_Id(String user_oid,String boardId);
}
