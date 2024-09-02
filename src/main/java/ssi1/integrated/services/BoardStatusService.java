package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.project_board.board_status.BoardStatus;
import ssi1.integrated.project_board.board_status.BoardStatusRepository;
import ssi1.integrated.project_board.status.Status;

import java.util.List;
@Service
public class BoardStatusService {
    @Autowired
    private BoardStatusRepository boardStatusRepository;

    public List<BoardStatus> getAllBoardStatus(){
        return boardStatusRepository.findAll();
    }

//    public List<Status> getStatusList(String boardId){
//
//    }
}
