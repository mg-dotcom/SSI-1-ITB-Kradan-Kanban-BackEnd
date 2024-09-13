package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board_status.BoardStatus;
import ssi1.integrated.project_board.board_status.BoardStatusRepository;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BoardStatusService {
    @Autowired
    private BoardStatusRepository boardStatusRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private BoardRepository boardRepository;

    public List<BoardStatus> getAllBoardStatus(){
        return boardStatusRepository.findAll();
    }

    public BoardStatus addStatusBoard(Integer statusId,String boardId){
        Optional<Status> status=statusRepository.findById(statusId);
        Optional<Board> board=boardRepository.findById(boardId);

        if (status.isEmpty() || board.isEmpty()) {
            throw new IllegalStateException("Status or Board not found");
        }

        BoardStatus newBoardStatus=new BoardStatus();
        newBoardStatus.setBoard(board.get());
        newBoardStatus.setStatus(status.get());
        return boardStatusRepository.save(newBoardStatus);
    }

}
