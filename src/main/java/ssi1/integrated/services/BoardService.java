package ssi1.integrated.services;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.security.JwtAuthenticationFilter;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserDTO;
import ssi1.integrated.user_account.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BoardStatusService boardStatusService;


    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public BoardDTO createBoard(String boardName) {
        // Extract the JWT payload from the request
        JwtPayload jwtPayload = jwtAuthenticationFilter.getJwtPayload(request);

        if (jwtPayload == null) {
            // Handle the case where the JWT payload is null, e.g., return an error or throw an exception
            throw new IllegalStateException("JWT Payload is null");
        }

        // Find the user associated with the OID from the JWT payload
        Optional<User> user = userRepository.findByOid(jwtPayload.getOid());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        if (user.isEmpty()) {
            // Handle the case where the user is not found
            throw new IllegalStateException("User not found");
        }

        // Create a new Board object and set its name and user
        Board newBoard = new Board();
        newBoard.setName(boardName);
        newBoard.setUser_oid(user.get().getOid());
        newBoard.setMaximumTask(10);
        newBoard.setLimitMaximumTask(true);

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
        Optional<Board> board = boardRepository.findById(boardId);
        Optional<User> user = userRepository.findByOid(board.get().getUser_oid());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        boardDTO.setOwner(userDTO);
        return boardDTO;
    }
}
