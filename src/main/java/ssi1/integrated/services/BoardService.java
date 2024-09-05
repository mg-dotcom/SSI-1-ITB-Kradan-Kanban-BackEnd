package ssi1.integrated.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.dtos.CreateBoardDTO;
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

        //set default status in every new board
        boardStatusService.addStatusBoard(1, newBoard.getId());
        boardStatusService.addStatusBoard(2, newBoard.getId());
        boardStatusService.addStatusBoard(3, newBoard.getId());
        boardStatusService.addStatusBoard(4, newBoard.getId());
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

    public String deleteBoard(String boardId){
        boardRepository.findById(boardId).orElseThrow(
                ()-> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        boardRepository.deleteById(boardId);
        return "BOARD ID "+boardId+" DELETED";
    }
}
