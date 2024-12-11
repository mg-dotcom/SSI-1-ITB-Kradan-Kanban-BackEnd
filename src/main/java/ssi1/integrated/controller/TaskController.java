package ssi1.integrated.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.dtos.TaskFileDTO;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task_attachment.TaskFile;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.TaskFileService;
import ssi1.integrated.services.TaskService;

import java.io.IOException;
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

    @GetMapping("/{boardId}/tasks/{taskId}/files/{fileName}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String boardId,
            @PathVariable Integer taskId,
            @PathVariable String fileName,
            @RequestHeader(name = "Authorization", required = false) String accessToken) {
        try {
            return taskFileService.getFileForPreview(boardId, taskId, fileName, accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving file: " + fileName, e);
        }
    }

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<?> updateTaskAndFiles(
            @PathVariable String boardId,
            @PathVariable Integer taskId,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "taskDto", required = false) String newTaskDTO,
            @RequestHeader(name = "Authorization") String accessToken) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        NewTaskDTO taskDto = objectMapper.readValue(newTaskDTO, NewTaskDTO.class);

        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        if (newTaskDTO == null || taskDto.getTitle() == null) {
            return ResponseEntity.badRequest().body("Missing required task details");
        }

        boardService.getBoardById(boardId);

        if (taskDto.getId() == null) {
            taskDto.setId(taskId);
        }

        service.updateTask(taskId, taskDto, boardId, jwtToken);

        List<TaskFileDTO> existingFiles = taskFileService.getFilesByTaskId(taskId, boardId, accessToken);
        Map<String, TaskFileDTO> existingFileMap = existingFiles.stream()
                .collect(Collectors.toMap(TaskFileDTO::getFileName, file -> file));

        Set<String> updatedFileNames = new HashSet<>();
        List<MultipartFile> filesToSave = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();

                if (fileName == null || fileName.isEmpty()) {
                    continue;
                }

                updatedFileNames.add(fileName);

                if (existingFileMap.containsKey(fileName)) {
                    TaskFileDTO existingFile = existingFileMap.get(fileName);
                    taskFileService.deleteFileById(boardId, existingFile.getId(), taskId, jwtToken);
                }

                filesToSave.add(file);
            }

            for (TaskFileDTO existingFile : existingFiles) {
                if (!updatedFileNames.contains(existingFile.getFileName())) {
                    taskFileService.deleteFileById(boardId, existingFile.getId(), taskId, jwtToken);
                }
            }
        } else {
            for (TaskFileDTO existingFile : existingFiles) {
                taskFileService.deleteFileById(boardId, existingFile.getId(), taskId, jwtToken);
            }
        }

        List<TaskFile> savedFiles = taskFileService.saveAllFilesList(taskId, filesToSave, boardId, jwtToken);

        taskDto.setFiles(savedFiles);

        return ResponseEntity.ok(taskDto);
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


}
