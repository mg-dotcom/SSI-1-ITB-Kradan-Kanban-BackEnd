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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<CollaboratorDTO> collaborators=collabBoardService.getAllCollabsBoard(accessToken, boardId);
        Map<String, Object> response = new HashMap<>();
        response.put("collaborators", collaborators);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}/collabs/{collabsOid}")
    public ResponseEntity<?> getCollaborators(
            @RequestHeader(name = "Authorization", required = false) String accessToken,
            @PathVariable String boardId,
            @PathVariable String collabsOid) {
        return ResponseEntity.ok(collabBoardService.getCollaborators(accessToken, boardId, collabsOid));
    }


    @PostMapping("/{boardId}/collabs")
    public ResponseEntity<CollabBoardDTO> addCollabBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken, @RequestBody @Valid AddCollabBoardDTO addCollabBoardDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collabBoardService.addCollabBoard(accessToken,boardId,addCollabBoardDTO));
    }

    @PatchMapping("/{boardId}/collabs/{collab_oid}")
    public CollabBoard updateCollaboratorAccessRight(@RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collab_oid,@RequestBody AccessRightDTO accessRight){
        return collabBoardService.updateCollaboratorAccessRight(accessToken,boardId,collab_oid,accessRight);
    }

    @DeleteMapping("/{boardId}/collabs/{collab_oid}")
    public void deleteCollaborator(@RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collab_oid){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        collabBoardService.deleteCollaborator(jwtToken,boardId,collab_oid);
    }

}
