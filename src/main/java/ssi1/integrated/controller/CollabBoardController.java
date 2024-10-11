package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.AddCollabBoardDTO;
import ssi1.integrated.dtos.CollabBoardDTO;
import ssi1.integrated.dtos.CollaboratorDTO;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.services.CollabBoardService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class CollabBoardController {
    @Autowired
    private CollabBoardService collabBoardService;
    @GetMapping("/{boardId}/collabs")
    public List<CollaboratorDTO> getAllCollaborators(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return collabBoardService.getAllCollabsBoard(jwtToken,boardId);
    }
    @GetMapping("/{boardId}/collabs/{collabsOid}")
    public CollaboratorDTO getCollaborators( @RequestHeader(name = "Authorization") String accessToken,@PathVariable String boardId,@PathVariable String collabsOid) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return collabBoardService.getCollaborators(jwtToken,boardId,collabsOid);
    }

    @PostMapping("/{boardId}/collabs")
    public CollabBoardDTO addCollabBoard(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken, @RequestBody AddCollabBoardDTO addCollabBoardDTO) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return collabBoardService.addCollabBoard(jwtToken,boardId,addCollabBoardDTO);
    }

}
