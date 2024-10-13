package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.AccessRightDTO;
import ssi1.integrated.dtos.AddCollabBoardDTO;
import ssi1.integrated.dtos.CollabBoardDTO;
import ssi1.integrated.dtos.CollaboratorDTO;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.CollabBoardService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class CollabBoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private CollabBoardService collabBoardService;
    @GetMapping("/{boardId}/collabs")
    public ResponseEntity<?> getAllCollaborators(
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {

        Board board = boardService.getBoardById(boardId);

        // If the board is public, allow access without token
        if (board.getVisibility() == Visibility.PUBLIC) {
            return ResponseEntity.ok(collabBoardService.getAllCollabsBoard(null, boardId));
        }

        // If the board is private, check if the token is present and valid
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String jwtToken = accessToken.substring(7);
            return ResponseEntity.ok(collabBoardService.getAllCollabsBoard(jwtToken, boardId));
        }

        // If no token is provided and the board is private, return access denied
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied to private board with BOARD ID: " + boardId);
    }

    @GetMapping("/{boardId}/collabs/{collabsOid}")
    public ResponseEntity<?> getCollaborators(
            @RequestHeader(name = "Authorization", required = false) String accessToken,
            @PathVariable String boardId,
            @PathVariable String collabsOid) {

        Board board = boardService.getBoardById(boardId);

        // If the board is public, allow access without token
        if (board.getVisibility() == Visibility.PUBLIC) {
            return ResponseEntity.ok(collabBoardService.getCollaborators(null, boardId, collabsOid));
        }

        // If the board is private, check if the token is present and valid
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String jwtToken = accessToken.substring(7);
            return ResponseEntity.ok(collabBoardService.getCollaborators(jwtToken, boardId, collabsOid));
        }

        // If no token is provided and the board is private, return access denied
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied to private board with BOARD ID: " + boardId);
    }


    @PostMapping("/{boardId}/collabs")
    public ResponseEntity<CollabBoardDTO> addCollabBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken, @RequestBody @Valid AddCollabBoardDTO addCollabBoardDTO) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return ResponseEntity.status(HttpStatus.CREATED).body(collabBoardService.addCollabBoard(jwtToken,boardId,addCollabBoardDTO));
    }

    @PatchMapping("/{boardId}/collabs/{collab_oid}")
    public CollabBoard updateCollaboratorAccessRight(@RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collab_oid,@RequestBody AccessRightDTO accessRight){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return collabBoardService.updateCollaboratorAccessRight(jwtToken,boardId,collab_oid,accessRight);
    }

    @DeleteMapping("/{boardId}/collabs/{collab_oid}")
    public void deleteCollaborator(@RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collab_oid){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        collabBoardService.deleteCollaborator(jwtToken,boardId,collab_oid);
    }

}
