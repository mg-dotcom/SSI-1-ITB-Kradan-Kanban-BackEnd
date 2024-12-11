package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ssi1.integrated.FileStorageProperties;
import ssi1.integrated.configs.ListMapper;
import ssi1.integrated.dtos.FileInfoDTO;
import ssi1.integrated.dtos.TaskFileDTO;
import ssi1.integrated.exception.handler.FileUploadException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task.TaskRepository;
import ssi1.integrated.project_board.task_attachment.TaskFile;
import ssi1.integrated.project_board.task_attachment.TaskFileRepository;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Getter
public class TaskFileService {
    @Autowired
    private TaskFileRepository fileRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private CollabBoardRepository collabBoardRepository;

    private static final int MAX_FILES = 10;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;
    private final Path fileStorageLocation;

    // create folder product-image
    @Autowired
    public TaskFileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties
                .getUploadDir()).toAbsolutePath().normalize();
        try {
            // if have same folder not create
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public ResponseEntity<Resource> getFileForPreview(
            String boardId, Integer taskId, String fileName, String accessToken) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));

        // Step 1: Fetch the board and validate access
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId));

        // Step 2: Access control for visibility of the board
        Visibility visibility = board.getVisibility();

        // Extract the JWT token if available
        String jwtToken = (accessToken != null && accessToken.startsWith("Bearer "))
                ? accessToken.substring(7)
                : accessToken;

        // Check if the user is the board owner or a collaborator
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken, boardId);

        // If the board is private and the user is neither the owner nor a collaborator, deny access
        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        // Step 3: Resolve the file path
        Path taskDirectory = this.fileStorageLocation.resolve(existingTask.getId().toString());

        Path targetLocation = taskDirectory.resolve(fileName);

        Path filePath = targetLocation;

        Resource resource = new FileSystemResource(filePath);

        // Check if the file exists
        if (!resource.exists()) {
            throw new ItemNotFoundException("File not found: " + fileName);
        }

        try {
            // Step 4: Determine the content type of the file
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Default content type if not detected
            }

            // Step 5: Return the file as a downloadable resource
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error loading file: " + fileName, e);
        }
    }



    public List<TaskFileDTO> getFilesByTaskId(Integer taskId, String boardId, String accessToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        List<TaskFile> allFiles = fileRepository.findAllByTaskId(taskId);
        List<TaskFileDTO> mappedFiles = allFiles.stream().map(taskFile -> new TaskFileDTO(taskFile)).collect(Collectors.toList());
        Visibility visibility = board.getVisibility();

        if (visibility == Visibility.PUBLIC) {
            return mappedFiles;
        }

        String jwtToken = accessToken != null && accessToken.startsWith("Bearer ")
                ? accessToken.substring(7)
                : accessToken;

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken, boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        return mappedFiles;
    }


    @Transactional
    public List<TaskFile> saveAllFilesList(Integer taskId, List<MultipartFile> fileList, String boardId, String jwtToken) {
        // ! Checking security
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken, boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can edit tasks.");
        }

        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
        }

        StringBuilder errorMessages = new StringBuilder();
        List<FileInfoDTO> errorFiles = new ArrayList<>();

        // * Define individual error messages
        String duplicateFileMessage = "Filenames must be unique within the task.";
        String maxFilesMessage = "Each task can have at most " + MAX_FILES + " files.";
        String maxFilesExceedMessage = "Each file cannot be larger than " + (MAX_FILE_SIZE / (1024 * 1024)) + " MB.";

        // ! 1. Check for duplicate filenames
        List<FileInfoDTO> duplicateFileInfos = fileList.stream()
                .filter(file -> taskRepository.existsByTaskIdAndFileName(taskId, file.getName()))
                .map(file -> new FileInfoDTO(file.getName(), file.getSize()))
                .toList();

        if (!duplicateFileInfos.isEmpty()) {
            appendErrorMessage(errorMessages, duplicateFileMessage, duplicateFileInfos, errorFiles);
        }

        // ! 2. Check for files exceeding the maximum file size
        List<FileInfoDTO> exceedFileInfos = fileList.stream()
                .filter(file -> file.getSize() > MAX_FILE_SIZE)
                .map(file -> new FileInfoDTO(file.getName(), file.getSize()))
                .toList();

        if (!exceedFileInfos.isEmpty()) {
            appendErrorMessage(errorMessages, maxFilesExceedMessage, exceedFileInfos, errorFiles);
        }

        // ! 3. Check for exceeding the file count limit
        int currentFileCount = taskRepository.countFilesByTaskId(taskId);
        int allowedFilesCount = MAX_FILES - currentFileCount;

        List<FileInfoDTO> excessFiles = fileList.stream()
                .filter(file -> !duplicateFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getName()))
                        && !exceedFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getName())))
                .skip(allowedFilesCount)
                .map(file -> new FileInfoDTO(file.getName(), file.getSize()))
                .toList();

        if (!excessFiles.isEmpty()) {
            appendErrorMessage(errorMessages, maxFilesMessage, excessFiles, errorFiles);
        }

        // * Save valid files if no errors
        List<TaskFile> validFiles = fileList.stream()
                .filter(file -> !duplicateFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getName()))
                        && !exceedFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getName()))
                        && !excessFiles.stream().anyMatch(f -> f.getFileName().equals(file.getName())))
                .limit(allowedFilesCount)
                .map(file -> {
                    // Create a subdirectory named after the task ID
                    Path taskDirectory = this.fileStorageLocation.resolve(existingTask.getId().toString());

                    try {
                        // Create the directory for the task if it doesn't exist
                        if (!Files.exists(taskDirectory)) {
                            Files.createDirectories(taskDirectory);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException("Could not create directory for task ID: " + taskId, ex);
                    }

                    // Resolve the file path within the task-specific directory
                    Path targetLocation = taskDirectory.resolve(file.getOriginalFilename());

                    // Create the TaskFile object
                    TaskFile taskFile = new TaskFile();
                    taskFile.setFileName(file.getOriginalFilename());
                    taskFile.setFileSize(file.getSize());
                    taskFile.setFilePath(targetLocation.toString());
                    taskFile.setContentType(file.getContentType());
                    taskFile.setCreatedOn(ZonedDateTime.now());
                    taskFile.setTask(existingTask);

                    try {
                        // Copy the file to the task-specific directory
                        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                        fileRepository.save(taskFile);
                    } catch (IOException ex) {
                        throw new RuntimeException("Could not store file " + file.getName() + " in task directory " + taskId, ex);
                    }

                    return taskFile;
                })
                .toList();

        if (!validFiles.isEmpty()) {
            fileRepository.saveAll(validFiles);
        }

        if (errorMessages.length() > 0) {
            errorMessages.append(" The following files are not added:");
            throw new FileUploadException(errorMessages.toString(), errorFiles);
        }
        return validFiles;
    }

    @Transactional
    public TaskFileDTO deleteFileById(String boardId, Integer fileId, Integer taskId, String jwtToken) {
        // Checking security
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));

        TaskFile file = fileRepository.findById(fileId).orElseThrow(() -> new ItemNotFoundException("File not found with FILE ID: " + fileId));

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        Visibility visibility = board.getVisibility();
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaboratorWrite = isCollaboratorWriteAccess(jwtToken, boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (visibility == Visibility.PUBLIC && !isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException("Only board owner and collaborators with write access can edit tasks.");
        }

        if (!isOwner && !isCollaboratorWrite) {
            throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
        }

        // Deleting the file from the file system
        try {
            // Define the path of the file in the storage location
            Path taskDirectory = this.fileStorageLocation.resolve(existingTask.getId().toString());

            Path targetLocation = taskDirectory.resolve(file.getFileName()); // Assuming `file.getFileName()` gives the file name

            // Delete the file from the file system
            Files.delete(targetLocation); // This will delete the file from the storage
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            throw new RuntimeException("Error deleting file from filesystem", e);
        }

        // Now delete the file from the database
        fileRepository.delete(file);

        // Return a TaskFileDTO for the deleted file (if needed)
        return new TaskFileDTO(file);
    }


    private void appendErrorMessage(StringBuilder errorMessages, String message, List<FileInfoDTO> fileInfos, List<FileInfoDTO> errorFiles) {
        if (errorMessages.length() > 0) {
            errorMessages.append(" , ");
        }
        errorMessages.append(message);

        errorFiles.addAll(fileInfos);
    }

    private boolean isBoardOwner(String userOid, String jwtToken) {
        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);
        User user = userService.getUserByOid(userOid);
        return user.getOid().equals(jwtPayload.getOid());
    }
    public boolean isCollaboratorWriteAccess(String jwtToken, String boardId) {
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());

        return collaborator != null && collaborator.getAccessRight() == AccessRight.WRITE;
    }

    public boolean isCollaborator(String jwtToken, String boardId){
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());
        return collaborator!=null;
    }

}

