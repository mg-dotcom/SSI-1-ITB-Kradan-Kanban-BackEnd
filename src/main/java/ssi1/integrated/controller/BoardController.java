package ssi1.integrated.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.dtos.BoardVisibilityDTO;
import ssi1.integrated.dtos.CreateBoardDTO;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.user_account.User;
import ssi1.integrated.user_account.UserRepository;

import io.jsonwebtoken.JwtException; // Replace with the actual package for JwtException
import io.jsonwebtoken.ExpiredJwtException; // Replace with the actual package for ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException; // Replace with the actual package for MalformedJwtException
import io.jsonwebtoken.SignatureException; // Replace with the actual package for SignatureException

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/all")
    public List<Board>getAllBoards(){
        return boardService.getAllBoards();
    }

    @PostMapping("")
    public ResponseEntity<BoardDTO> createBoard(@RequestHeader (name="Authorization")String accessToken,@Valid @RequestBody CreateBoardDTO boardDTO){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(jwtToken,boardDTO));
    }

    @GetMapping("")
    public List<Board> getBoardByUser(@RequestHeader (name="Authorization")String accessToken){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return boardService.getAllBoards(jwtToken);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDTO> getBoardDetail(
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {
        Board board = boardService.getBoardById(boardId);

        if (accessToken == null && board.getVisibility() == Visibility.PUBLIC) {
            return ResponseEntity.ok(boardService.getBoardDetail(boardId, null));
        }

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            return ResponseEntity.ok(boardService.getBoardDetail(boardId, jwtToken));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{boardId}")
    public String deleteBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization")String accessToken){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return boardService.deleteBoard(boardId, jwtToken);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardVisibilityDTO> setBoardVisibility(
            @PathVariable String boardId,
            @RequestBody @Valid BoardVisibilityDTO boardVisibilityDTO, @RequestHeader(name = "Authorization")String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return ResponseEntity.ok(boardService.changeVisibility(boardId,boardVisibilityDTO,jwtToken));
    }

    @GetMapping("/visibility/{boardId}")
    public Visibility getBoardVisibility(@PathVariable String boardId){
        return boardRepository.findVisibilityByBoardId(boardId);
    }
}
