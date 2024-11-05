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
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB in bytes


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


    public void saveAllFilesList(Integer taskId, List<TaskFile> fileList) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));

        List<FileInfoDTO> duplicateFileInfos = fileList.stream()
                .filter(file -> taskRepository.existsByTaskIdAndFileName(taskId, file.getFileName()))
                .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                .collect(Collectors.toList());

        if (!duplicateFileInfos.isEmpty()) {
            String message = "Filenames must be unique within the task. The following files are not added due to duplicate names:";
            throw new FileUploadException(message, duplicateFileInfos);
        }

        List<FileInfoDTO> exceedFileInfos = fileList.stream()
                .filter(file -> file.getFileSize() > MAX_FILE_SIZE)
                .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                .collect(Collectors.toList());

        int currentFileCount = taskRepository.countFilesByTaskId(taskId);
        int allowedFilesCount = MAX_FILES - currentFileCount;

        List<TaskFile> validFiles = fileList.stream()
                .filter(file -> file.getFileSize() <= MAX_FILE_SIZE &&
                        !taskRepository.existsByTaskIdAndFileName(taskId, file.getFileName()))
                .limit(allowedFilesCount)
                .map(file -> {
                    TaskFile taskFile = new TaskFile();
                    taskFile.setFileName(file.getFileName());
                    taskFile.setFileSize(file.getFileSize());
                    taskFile.setCreatedOn(ZonedDateTime.now());
                    taskFile.setTask(existingTask);
                    return taskFile;
                })
                .collect(Collectors.toList());

        if (validFiles.size() < fileList.size()) {
            List<FileInfoDTO> excessCountFiles = fileList.stream()
                    .filter(file -> file.getFileSize() <= MAX_FILE_SIZE) // Only valid size files
                    .skip(allowedFilesCount)
                    .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                    .collect(Collectors.toList());
            exceedFileInfos.addAll(excessCountFiles); // Include files exceeding the MAX_FILES limit
        }

        if (!exceedFileInfos.isEmpty()) {
            String message = "Each task can have at most " + MAX_FILES + " files and each file cannot be larger than " +
                    (MAX_FILE_SIZE / (1024 * 1024)) + " MB. The following files are not added due to size limit:";
            throw new FileUploadException(message, exceedFileInfos);
        }

        fileRepository.saveAll(validFiles);
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

