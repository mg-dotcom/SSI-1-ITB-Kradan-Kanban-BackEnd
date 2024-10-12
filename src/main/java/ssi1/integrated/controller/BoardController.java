package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.AllBoardDTO;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.dtos.BoardVisibilityDTO;
import ssi1.integrated.dtos.CreateBoardDTO;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.services.BoardService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @Autowired
    public BoardController(BoardService boardService, BoardRepository boardRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

    @GetMapping("/all")
    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

    @PostMapping("")
    public ResponseEntity<BoardDTO> createBoard(@RequestHeader(name = "Authorization") String accessToken, @Valid @RequestBody(required = false) CreateBoardDTO boardDTO) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(jwtToken, boardDTO));
    }

    @GetMapping("")
    public AllBoardDTO getBoardByUser(@RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return boardService.getAllBoards(jwtToken);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoardDetail(
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {

        Board board = boardService.getBoardById(boardId);

        // If the board is public, allow access without token
        if (board.getVisibility() == Visibility.PUBLIC) {
            return ResponseEntity.ok(boardService.getBoardDetail(boardId, null));
        }

        // If the board is private, check if the token is present and valid
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String jwtToken = accessToken.substring(7);
            return ResponseEntity.ok(boardService.getBoardDetail(boardId, jwtToken));
        }

        // If no token is provided and the board is private, return access denied
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied to private board with BOARD ID: " + boardId);
    }


    @DeleteMapping("/{boardId}")
    public String deleteBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return boardService.deleteBoard(boardId, jwtToken);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardVisibilityDTO> setBoardVisibility(
            @PathVariable String boardId,
            @RequestBody(required = false) BoardVisibilityDTO boardVisibilityDTO, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return ResponseEntity.ok(boardService.changeVisibility(boardId, boardVisibilityDTO, jwtToken));
    }

    @GetMapping("/visibility/{boardId}")
    public Visibility getBoardVisibility(@PathVariable String boardId) {
        return boardRepository.findVisibilityByBoardId(boardId);
    }
}
