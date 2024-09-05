package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.EditLimitDTO;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.StatusService;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/statuses")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private BoardService boardService;




    @GetMapping("/{boardId}")
    public List<Status> getAllStatus(@PathVariable String boardId){
        return  statusService.getAllStatus(boardId);
    }

    @GetMapping("/{boardId}/{statusId}")
    public Status getStatusById(@PathVariable String boardId,@PathVariable Integer statusId){
        return statusService.getStatusById(boardId,statusId);
    }

    @PutMapping("/{boardId}/{statusId}")
    public ResponseEntity<NewStatusDTO> updateStatus(@PathVariable String boardId,@PathVariable Integer statusId,@Valid @RequestBody NewStatusDTO updateStatus) {
        return ResponseEntity.ok(statusService.updateStatus(boardId,statusId, updateStatus));
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<NewStatusDTO> addStatus(@PathVariable String boardId,@Valid @RequestBody NewStatusDTO newStatusDTO){
        NewStatusDTO createdStatus = statusService.insertNewStatus(boardId,newStatusDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @DeleteMapping("/{boardId}/{statusId}")
    public Status deleteStatus(@PathVariable String boardId,@PathVariable Integer statusId){
        return statusService.deleteStatus(boardId,statusId);
    }
    @DeleteMapping("/{boardId}/{statusId}/{newStatusId}")
    public ResponseEntity<Status> transfer(@PathVariable String boardId,@PathVariable Integer statusId,  @PathVariable Integer newStatusId) {

        return ResponseEntity.ok(statusService.transferStatus(boardId,statusId,newStatusId));
    }


}
