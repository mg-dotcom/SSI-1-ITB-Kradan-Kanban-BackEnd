package ssi1.integrated.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.*;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.CollabBoardService;
import ssi1.integrated.services.InvitationService;

import java.io.UnsupportedEncodingException;
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
    @Autowired
    private InvitationService invitationService;
    @GetMapping("/{boardId}/collabs")
    public ResponseEntity<List<CollaboratorDTO>> getAllCollaborators(
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {

        // Fetch the list of collaborators
        List<CollaboratorDTO> collaborators = collabBoardService.getAllCollabsBoard(accessToken, boardId);

        // Directly return the list of collaborators
        return ResponseEntity.ok(collaborators);
    }

    @GetMapping("/{boardId}/collabs/{collabsOid}")
    public ResponseEntity<?> getCollaborators(
            @RequestHeader(name = "Authorization", required = false) String accessToken,
            @PathVariable String boardId,
            @PathVariable String collabsOid) {
        return ResponseEntity.ok(collabBoardService.getCollaborators(accessToken, boardId, collabsOid));
    }

    @PostMapping("/{boardId}/collabs")
    public ResponseEntity<CollabBoardDTO> addCollabBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken, @RequestBody AddCollabBoardDTO addCollabBoardDTO) throws MessagingException, UnsupportedEncodingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(collabBoardService.addCollabBoard(accessToken,boardId,addCollabBoardDTO));
    }

    @PatchMapping("/{boardId}/collabs/{collab_oid}")
    public CollabBoard updateCollaboratorAccessRight(@RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collab_oid,@RequestBody AccessRightDTO accessRight){
        return collabBoardService.updateCollaboratorAccessRight(accessToken,boardId,collab_oid,accessRight);
    }
    @PatchMapping("/{boardId}/collabs/invitations")
    public CollabBoard invitationCollab(@RequestHeader(name = "Authorization") String accessToken,
                                        @PathVariable String boardId,
                                        @RequestBody(required = false) InvitationDTO invitationDTO) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return invitationService.invitationCollab(jwtToken,boardId,invitationDTO);
    }

    @GetMapping("/{boardId}/collabs/invitations")
    public BoardInvitationDTO getInvitation(@RequestHeader(name = "Authorization") String accessToken,
                                        @PathVariable String boardId) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return invitationService.getInvitaionStatus(jwtToken,boardId);
    }

    @DeleteMapping("/{boardId}/collabs/{collab_oid}")
    public void deleteCollaborator(@RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collab_oid){
        collabBoardService.deleteCollaborator(accessToken,boardId,collab_oid);
    }
}
