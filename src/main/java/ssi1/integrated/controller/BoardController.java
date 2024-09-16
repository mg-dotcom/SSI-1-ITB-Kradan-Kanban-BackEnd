package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.BoardDTO;
import ssi1.integrated.dtos.CreateBoardDTO;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.services.BoardService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;


    @GetMapping("/all")
    public List<Board>getAllBoards(){
        return boardService.getAllBoards();
    }

    @PostMapping("")
    public ResponseEntity<BoardDTO> createBoard(@RequestHeader (name="Authorization")String token,@Valid @RequestBody CreateBoardDTO boardDTO){
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(jwtToken,boardDTO));
    }

    @GetMapping("")
    public List<Board> getBoardByUser(@RequestHeader (name="Authorization")String token){
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return boardService.getAllBoards(jwtToken);
    }

    @GetMapping("/{boardId}")
    public BoardDTO getBoardDetail(@PathVariable String boardId){
        return boardService.getBoardDetail(boardId);
    }

    @DeleteMapping("/{boardId}")
    public String deleteBoard(@PathVariable String boardId){

        return boardService.deleteBoard(boardId);
    }

}
