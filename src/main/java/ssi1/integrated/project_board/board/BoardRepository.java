package ssi1.integrated.project_board.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssi1.integrated.dtos.BoardVisibilityDTO;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,String> {
    List<Board> findByUserOid(String userOid);

    @Query("SELECT b.visibility FROM Board b WHERE b.id = :boardId")
    Visibility findVisibilityByBoardId(String boardId);

}
