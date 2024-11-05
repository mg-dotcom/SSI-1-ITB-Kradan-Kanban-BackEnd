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
    private StatusRepository statusRepository;
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


    public void saveAllFilesList (Integer taskId, List<TaskFile> fileList)  {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));

        int currentFileCount = taskRepository.countFilesByTaskId(taskId);

        // Check if adding all the new files would exceed the limit
        if (currentFileCount + fileList.size() > MAX_FILES) {
            int allowedFilesCount = MAX_FILES - currentFileCount;

            List<TaskFile> filesWithinLimit = fileList.stream()
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
            List<FileInfoDTO> excessFileInfos = fileList.stream()
                    .skip(allowedFilesCount)
                    .map(file -> new FileInfoDTO(file.getFileName(), file.getFileSize()))
                    .collect(Collectors.toList());

            fileRepository.saveAll(filesWithinLimit);
            throw new FileUploadException("Each task can have at most "+ MAX_FILES +" files. The following files are not added:",excessFileInfos);
        }


        // Check if each file exceed 20MB
        for (TaskFile file : fileList) {
            //            if (file.getFileSize() > MAX_FILE_SIZE) {
//               fileErrors.add("File " + file.getFileName() + " exceeds the maximum file size of 20 MB.");
//            }
            TaskFile attachment = new TaskFile();
            attachment.setFileName(file.getFileName());
            attachment.setFileSize(file.getFileSize());
            attachment.setCreatedOn(file.getCreatedOn());
            attachment.setTask(existingTask);
            fileRepository.save(attachment);
        }

        // If within limit, save all files
        fileRepository.saveAll(fileList);
    }

    // Check if user is the board owner
    private boolean isBoardOwner(String userOid, String jwtToken) {
        JwtPayload jwtPayload=jwtService.extractPayload(jwtToken);
        User user = userService.getUserByOid(userOid);
        return user.getOid().equals(jwtPayload.getOid());
    }

    // Check if collaborator has write access
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

