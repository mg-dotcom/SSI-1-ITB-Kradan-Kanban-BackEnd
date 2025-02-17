package ssi1.integrated.controller;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.*;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.services.CollabBoardService;
import ssi1.integrated.services.InvitationService;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class CollabBoardController {
    private final CollabBoardService collabBoardService;
    private final InvitationService invitationService;

    @Autowired
    public CollabBoardController(CollabBoardService collabBoardService, InvitationService invitationService) {
        this.collabBoardService = collabBoardService;
        this.invitationService = invitationService;
    }

    @GetMapping("/{boardId}/collabs")
    public ResponseEntity<List<CollaboratorDTO>> getAllCollaborators(
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {

        List<CollaboratorDTO> collaborators = collabBoardService.getAllCollabsBoard(accessToken, boardId);
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
    public ResponseEntity<CollabBoardDTO> addCollabBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken , @RequestHeader(name = "AccessTokenMS",required = false) String accessTokenMS , @RequestBody AddCollabBoardDTO addCollabBoardDTO) throws MessagingException, UnsupportedEncodingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(collabBoardService.addCollabBoard(accessToken,accessTokenMS,boardId,addCollabBoardDTO));
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
