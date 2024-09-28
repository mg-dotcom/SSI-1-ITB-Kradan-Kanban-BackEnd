package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.StatusService;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("v3/boards")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private BoardService boardService;

    @GetMapping("/{boardId}/statuses")
    public List<Status> getAllStatus(@PathVariable String boardId,   @RequestHeader(name = "Authorization")String accessToken){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return statusService.getAllStatus(boardId,jwtToken);
    }

    @GetMapping("/{boardId}/statuses/{statusId}")
    public Status getStatusById(@PathVariable String boardId,@PathVariable Integer statusId,  @RequestHeader(name = "Authorization")String accessToken){
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return statusService.getStatusById(boardId,statusId,jwtToken);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<NewStatusDTO> updateStatus(@PathVariable String boardId,@PathVariable Integer statusId,@Valid @RequestBody NewStatusDTO updateStatus,  @RequestHeader(name = "Authorization")String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return ResponseEntity.ok(statusService.updateStatus(boardId,statusId, updateStatus, jwtToken));
    }

    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<NewStatusDTO> addStatus(@PathVariable String boardId,@Valid @RequestBody NewStatusDTO newStatusDTO,  @RequestHeader(name = "Authorization")String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        NewStatusDTO createdStatus = statusService.insertNewStatus(boardId,newStatusDTO, jwtToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public Status deleteStatus(@PathVariable String boardId,@PathVariable Integer statusId,  @RequestHeader(name = "Authorization")String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return statusService.deleteStatus(boardId,statusId,jwtToken);
    }
    @DeleteMapping("/{boardId}/statuses/{statusId}/{newStatusId}")
    public ResponseEntity<Status> transfer(@PathVariable String boardId,@PathVariable Integer statusId,  @PathVariable Integer newStatusId, @RequestHeader(name = "Authorization")String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return ResponseEntity.ok(statusService.transferStatus(boardId,statusId,newStatusId,jwtToken));
    }


}
