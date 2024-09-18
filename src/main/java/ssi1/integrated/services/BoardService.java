package ssi1.integrated.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.dtos.CreateBoardDTO;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.security.JwtAuthenticationFilter;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserDTO;
import ssi1.integrated.user_account.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {
    private BoardRepository boardRepository;
    private UserService userService;
    private ModelMapper modelMapper;
    private BoardStatusService boardStatusService;
    private JwtService jwtService;
    private StatusService statusService;


    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public List<Board> getAllBoards(String token){
        JwtPayload jwtPayload = jwtService.extractPayload(token);

        if (jwtPayload == null) {
            throw new IllegalStateException("JWT Payload is null");
        }

        User user = userService.getUserByOid(jwtPayload.getOid());
        return boardRepository.findByUserOid(user.getOid());
    }

    public BoardDTO createBoard(String token,CreateBoardDTO createBoardDTO) {
        // Extract the JWT payload from the request
        JwtPayload jwtPayload = jwtService.extractPayload(token);

        // Find the user associated with the OID from the JWT payload
        User user = userService.getUserByOid(jwtPayload.getOid());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        // Create a new Board object and set its name and user
        Board newBoard = new Board();
        newBoard.setName(createBoardDTO.getName());
        newBoard.setUserOid(user.getOid());
        newBoard.setMaximumTask(10);
        newBoard.setLimitMaximumTask(true);
        newBoard.setEmoji(createBoardDTO.getEmoji());
        newBoard.setColor(createBoardDTO.getColor());

        // Save the new board to the repository
        boardRepository.save(newBoard);

        // Convert the Board entity to a BoardDTO
        BoardDTO boardDTO = modelMapper.map(newBoard, BoardDTO.class);
        boardDTO.setOwner(userDTO);

        //create default statuses
        NewStatusDTO noStatus=new NewStatusDTO();
        noStatus.setName("No Status");
        noStatus.setDescription("A status has not been assigned");
        noStatus.setStatusColor("#CCCCCC");

        NewStatusDTO todo=new NewStatusDTO();
        todo.setName("To Do");
        todo.setDescription("The task is included in the project");
        todo.setStatusColor("#FFA500");

        NewStatusDTO doing=new NewStatusDTO();
        doing.setName("Doing");
        doing.setDescription("The task is being worked on");
        doing.setStatusColor("#FF9A00");

        NewStatusDTO done=new NewStatusDTO();
        done.setName("Done");
        done.setDescription("The task has been completed");
        done.setStatusColor("#008000");

        statusService.insertNewStatus(newBoard.getId(), noStatus);
        statusService.insertNewStatus(newBoard.getId(), todo);
        statusService.insertNewStatus(newBoard.getId(), doing);
        statusService.insertNewStatus(newBoard.getId(), done);

        return boardDTO;

    }

    public BoardDTO getBoardDetail(String boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
        User user = userService.getUserByOid(board.getUserOid());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        boardDTO.setOwner(userDTO);
        return boardDTO;
    }

    public Board getBoardById(String boardId){
        return boardRepository.findById(boardId).orElseThrow(
                ()-> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );
    }

    public String deleteBoard(String boardId){
        boardRepository.findById(boardId).orElseThrow(
                ()-> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        boardStatusService.deleteStatusBoard(boardId);
        boardRepository.deleteById(boardId);
        return "BOARD ID "+boardId+" DELETED";
    }

}
