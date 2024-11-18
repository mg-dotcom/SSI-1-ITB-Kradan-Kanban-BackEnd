package ssi1.integrated.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
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
        System.out.println("sdfsdfsf");
        return ResponseEntity.ok(taskFileService.getFileById(boardId,fileId, accessToken));
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<?> updateTaskAndFiles(
            @PathVariable String boardId,
            @PathVariable Integer taskId,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "taskDto", required = false) String newTaskDTO,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException {

        // Deserialize taskDtoJson to a DTO object
        ObjectMapper objectMapper = new ObjectMapper();
        NewTaskDTO taskDto = objectMapper.readValue(newTaskDTO, NewTaskDTO.class);

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        // Ensure taskDto is valid
        if (newTaskDTO == null || taskDto.getTitle() == null) {
            return ResponseEntity.badRequest().body("Missing required task details");
        }

        // Ensure board exists
        boardService.getBoardById(boardId);

        // Set task ID if not present
        if (taskDto.getId() == null) {
            taskDto.setId(taskId);
        }

        // Update task details
        service.updateTask(taskId, taskDto, boardId, jwtToken);

        // Retrieve existing files
        List<TaskFileDTO> existingFiles = taskFileService.getFilesByTaskId(taskId, boardId, accessToken);
        Map<String, TaskFileDTO> existingFileMap = existingFiles.stream()
                .collect(Collectors.toMap(TaskFileDTO::getFileName, file -> file));

        Set<String> updatedFileNames = new HashSet<>();
        List<MultipartFile> filesToSave = new ArrayList<>(); // A list of MultipartFile objects

        // Process input files array
        if (files != null) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();

                // Skip if filename is empty or null
                if (fileName == null || fileName.isEmpty()) {
                    continue;
                }

                updatedFileNames.add(fileName);

                // If the file is not in existing files, it's a new file to add
                if (!existingFileMap.containsKey(fileName)) {
                    // Add the file to the list of files to save (MultipartFile)
                    filesToSave.add(file);
                }
            }

            // Delete files that are no longer in the updated list
            for (TaskFileDTO existingFile : existingFiles) {
                if (!updatedFileNames.contains(existingFile.getFileName())) {
                    taskFileService.deleteFileById(boardId, existingFile.getId(), taskId, jwtToken);
                }
            }
        } else {
            // If files is null, clear all existing files
            for (TaskFileDTO existingFile : existingFiles) {
                taskFileService.deleteFileById(boardId, existingFile.getId(), taskId, jwtToken);
            }
        }

        // Save new files to the task using the service
        List<TaskFile> savedFiles = taskFileService.saveAllFilesList(taskId, filesToSave, boardId, jwtToken);

        // Set the saved files in the taskDto
        taskDto.setFiles(savedFiles);

        return ResponseEntity.ok(taskDto); // Return taskDto as the response body
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
