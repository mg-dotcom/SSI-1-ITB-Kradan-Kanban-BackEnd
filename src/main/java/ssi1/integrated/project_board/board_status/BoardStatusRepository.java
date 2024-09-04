package ssi1.integrated.project_board.board_status;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardStatusRepository extends JpaRepository<BoardStatus, Integer> {

    List<BoardStatus> findByBoardId(String boardId);
}