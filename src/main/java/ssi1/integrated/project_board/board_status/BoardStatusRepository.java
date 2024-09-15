package ssi1.integrated.project_board.board_status;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface BoardStatusRepository extends JpaRepository<BoardStatus, Integer> {
    void deleteBoardStatusByBoardId(String boardId);
}