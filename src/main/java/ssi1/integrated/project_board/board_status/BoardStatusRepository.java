package ssi1.integrated.project_board.board_status;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface BoardStatusRepository extends JpaRepository<BoardStatus, Integer> {
    void deleteBoardStatusByBoardId(String boardId);

    List<BoardStatus> findByBoardId(String boardId);

    List<BoardStatus> findBoardStatusByStatusId(Integer statusId);

    BoardStatus findBoardStatusByBoard_IdAndStatus_Id(String boardId,Integer statusId);

}