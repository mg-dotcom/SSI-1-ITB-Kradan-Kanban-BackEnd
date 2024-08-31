package ssi1.integrated.services;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.BoardDTO;
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


    public List<Board>getAllBoards(){
        return boardRepository.findAll();
    }

    public BoardDTO createBoard(String boardName){
        JwtPayload jwtPayload = jwtAuthenticationFilter.getJwtPayload(request);
        Board newBoard=modelMapper.map(boardName,Board.class);
        BoardDTO boardDTO=modelMapper.map(newBoard,BoardDTO.class);
        Optional<User> user=userRepository.findByOid(jwtPayload.getOid());
        System.out.println(user);
        boardRepository.save(newBoard);
        return boardDTO;

    }
}
