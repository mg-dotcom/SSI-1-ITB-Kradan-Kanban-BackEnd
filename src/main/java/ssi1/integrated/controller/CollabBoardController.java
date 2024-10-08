package ssi1.integrated.controller;

import org.springframework.web.bind.annotation.*;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.services.CollabBoardService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class CollabBoardController {
    private CollabBoardService collabBoardService;
    @GetMapping("/{baordId}/collabs")
    public List<CollabBoard> getAllCollabBoards(@PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return collabBoardService.getAllCollabBoard(jwtToken,boardId);
    }
}
