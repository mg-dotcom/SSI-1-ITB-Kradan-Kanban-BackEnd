package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.TaskService;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")

public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    private BoardService boardService;


    @GetMapping("/{boardId}/tasks")
    public ResponseEntity<List<GeneralTaskDTO>> getAllTasks(
            @RequestParam(required = false, defaultValue = "createdOn") String sortBy,
            @RequestParam(required = false) List<String> filterStatuses,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken
    ) {
        return ResponseEntity.ok(service.getAllTasks(sortBy, filterStatuses, direction, boardId, accessToken));
    }

    @GetMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Integer taskId,
            @PathVariable String boardId,
            @RequestHeader(name = "Authorization", required = false) String accessToken
    ) {
        return ResponseEntity.ok(service.getTaskById(taskId, boardId, accessToken));
    }

    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<GeneralTaskDTO> addTask(@RequestBody(required = false) NewTaskDTO newTaskDTO, @PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insertNewTask(newTaskDTO, boardId, jwtToken));
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> deleteTask(@PathVariable Integer taskId, @PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return ResponseEntity.ok(service.removeTask(taskId, boardId, jwtToken));
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<NewTaskDTO> updateTask(@Valid @PathVariable Integer taskId, @RequestBody(required = false) NewTaskDTO newTaskDTO, @PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return ResponseEntity.ok(service.updateTask(taskId, newTaskDTO, boardId, jwtToken));
    }
}
