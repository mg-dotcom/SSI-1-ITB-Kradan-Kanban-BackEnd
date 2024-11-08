package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.project_board.task.TaskRepository;
import ssi1.integrated.project_board.task_attachment.TaskFile;
import ssi1.integrated.project_board.task_attachment.TaskFileRepository;
import ssi1.integrated.security.JwtPayload;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;
import ssi1.integrated.utils.FileSizeFormatter;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskFileService {
    @Autowired
    private TaskFileRepository fileRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private CollabBoardRepository collabBoardRepository;

    private static final int MAX_FILES = 10;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

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

    public TaskFileDTO getFileById(String boardId, Integer taskId, Integer fileId, String accessToken) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        TaskFile file = fileRepository.findById(fileId).orElseThrow(() -> new ItemNotFoundException("File not found with FILE ID: " + fileId));
        Visibility visibility = board.getVisibility();

        if (visibility == Visibility.PUBLIC) {
            return new TaskFileDTO(file);
        }

        String jwtToken = accessToken != null && accessToken.startsWith("Bearer ")
                ? accessToken.substring(7)
                : accessToken;

        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken, boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }
        return new TaskFileDTO(file);
    }

    public List<TaskFile> saveAllFilesList(Integer taskId, List<TaskFile> fileList, String boardId, String jwtToken) {
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

        boolean isExistingTask = taskRepository.existsById(taskId);

        if (!isExistingTask) {
            throw new ItemNotFoundException("Task not found with TASK ID: " + taskId);
        }

        StringBuilder errorMessages = new StringBuilder();
        List<FileInfoDTO> errorFiles = new ArrayList<>();

        // * Define individual error messages
        String duplicateFileMessage = "Filenames must be unique within the task.";
        String maxFilesMessage = "Each task can have at most " + MAX_FILES + " files.";
        String maxFilesExceedMessage = "Each file cannot be larger than " + (MAX_FILE_SIZE / (1024 * 1024)) + " MB.";

        // ! 1. Check for duplicate filenames
        List<FileInfoDTO> duplicateFileInfos = fileList.stream()
                .filter(file -> taskRepository.existsByTaskIdAndFileName(taskId, file.getFileName()))
                .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                .toList();

        if (!duplicateFileInfos.isEmpty()) {
            appendErrorMessage(errorMessages, duplicateFileMessage, duplicateFileInfos, errorFiles);
        }

        // ! 2. Check for files exceeding the maximum file size
        List<FileInfoDTO> exceedFileInfos = fileList.stream()
                .filter(file -> file.getFileSize() > MAX_FILE_SIZE)
                .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                .toList();

        if (!exceedFileInfos.isEmpty()) {
            appendErrorMessage(errorMessages, maxFilesExceedMessage, exceedFileInfos, errorFiles);
        }

        // ! 3. Check for exceeding the file count limit
        int currentFileCount = taskRepository.countFilesByTaskId(taskId);
        int allowedFilesCount = MAX_FILES - currentFileCount;

        List<FileInfoDTO> excessFiles = fileList.stream()
                .filter(file -> !duplicateFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getFileName()))
                        && !exceedFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getFileName())))
                .skip(allowedFilesCount)
                .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                .toList();

        if (!excessFiles.isEmpty()) {
            appendErrorMessage(errorMessages, maxFilesMessage, excessFiles, errorFiles);
        }

        // * Save valid files if no errors
        List<TaskFile> validFiles = fileList.stream()
                .filter(file -> !duplicateFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getFileName()))
                        && !exceedFileInfos.stream().anyMatch(f -> f.getFileName().equals(file.getFileName()))
                        && !excessFiles.stream().anyMatch(f -> f.getFileName().equals(file.getFileName())))
                .limit(MAX_FILES - currentFileCount) // Limit to the maximum allowed file count
                .map(file -> {
                    TaskFile taskFile = new TaskFile();
                    taskFile.setFileName(file.getFileName());
                    taskFile.setFileSize(file.getFileSize());
                    taskFile.setCreatedOn(ZonedDateTime.now());
                    taskFile.setTask(existingTask);
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

