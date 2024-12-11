package ssi1.integrated.project_board.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, String> {

    @Query("SELECT b.visibility FROM Board b WHERE b.id = :boardId")
    Visibility findVisibilityByBoardId(String boardId);

    List<Board> findAllByUserOidOrderByCreatedOnAsc(String userOid);

}