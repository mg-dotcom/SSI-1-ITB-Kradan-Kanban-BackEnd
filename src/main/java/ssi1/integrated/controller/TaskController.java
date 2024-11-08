package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.dtos.TaskFileDTO;
import ssi1.integrated.exception.handler.FileUploadException;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task_attachment.TaskFile;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.TaskFileService;
import ssi1.integrated.services.TaskService;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi1.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")
public class TaskController {
    @Autowired
    private TaskService service;
    @Autowired
    private BoardService boardService;
    @Autowired
    TaskFileService taskFileService;

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

    // FILE SERVICE
    @GetMapping("/{boardId}/tasks/{taskId}/files")
    public ResponseEntity<List<TaskFileDTO>> getFilesForTask(
            @PathVariable String boardId,
            @PathVariable Integer taskId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {
        return ResponseEntity.ok(taskFileService.getFilesByTaskId(taskId,boardId, accessToken));
    }


    @GetMapping("/{boardId}/tasks/{taskId}/files/{fileId}")
    public ResponseEntity<TaskFileDTO> getFileById(
            @PathVariable String boardId,
            @PathVariable Integer fileId,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {
        return ResponseEntity.ok(taskFileService.getFileById(boardId,fileId, accessToken));
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<?> updateTaskAndUploadFiles(
            @PathVariable String boardId,
            @PathVariable Integer taskId,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "taskDto", required = false) NewTaskDTO newTaskDTO,
            @RequestHeader(name = "Authorization") String accessToken) {

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);

        // ! Edit Task
        if (files == null || files.length == 0) {
            newTaskDTO.setFiles(new ArrayList<>());  // Ensure files is never null
        }
        if (newTaskDTO == null) {
            return ResponseEntity.badRequest().body("Missing 'taskDto' part");
        }
        if (newTaskDTO.getId() == null) {
            newTaskDTO.setId(taskId);
        }
        service.updateTask(taskId, newTaskDTO, boardId, jwtToken);

        // ! Edit Attachment
        if (files != null && files.length > 0) {
            List<TaskFile> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.isEmpty() || file.getOriginalFilename().isEmpty()) {
                    continue;
                }
                TaskFile taskFile = new TaskFile();
                taskFile.setFileName(file.getOriginalFilename());
                taskFile.setFileSize(file.getSize());
                taskFile.setCreatedOn(ZonedDateTime.now());
                fileList.add(taskFile);
            }
            List<TaskFile> savedFiles = taskFileService.saveAllFilesList(taskId, fileList, boardId, jwtToken);
            newTaskDTO.setFiles(savedFiles);
        }
        return ResponseEntity.ok(newTaskDTO);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}/files/{fileId}")
    public ResponseEntity<TaskFileDTO> deleteFile(
            @PathVariable Integer taskId,
            @PathVariable String boardId,
            @PathVariable Integer fileId,
            @RequestHeader(name = "Authorization") String accessToken)  {
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boardService.getBoardById(boardId);
        return ResponseEntity.ok(taskFileService.deleteFileById(boardId,fileId ,taskId , jwtToken));
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

//    @PutMapping("/{boardId}/tasks/{taskId}")
//    public ResponseEntity<NewTaskDTO> updateTask(@Valid @PathVariable Integer taskId, @RequestBody(required = false) NewTaskDTO newTaskDTO, @PathVariable String boardId, @RequestHeader(name = "Authorization") String accessToken) {
//        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
//        boardService.getBoardById(boardId);
//        return ResponseEntity.ok(service.updateTask(taskId, newTaskDTO, boardId, jwtToken));
//    }
}
