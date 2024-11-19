package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ssi1.integrated.FileStorageProperties;
import ssi1.integrated.configs.ListMapper;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.dtos.TaskFileDTO;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.exception.handler.LimitationException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.collab_management.AccessRight;
import ssi1.integrated.project_board.collab_management.CollabBoard;
import ssi1.integrated.project_board.collab_management.CollabBoardRepository;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
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
    private BoardRepository boardRepository;
    @Autowired
    private CollabBoardRepository collabBoardRepository;
    @Autowired
    private TaskFileRepository fileRepository;
    private final Path fileStorageLocation;

    // create folder product-image
    @Autowired
    public TaskService(FileStorageProperties fileStorageProperties) {
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

    public List<GeneralTaskDTO> getAllTasks(String sortBy, List<String> filterStatuses, String direction, String boardId, String accessToken) {
        // Fetch the board by ID
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
        );

        // Determine the sort order

        Sort.Order sortOrder = new Sort.Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );
        Sort sort = Sort.by(sortOrder);

        // Check board visibility
        Visibility visibility = board.getVisibility();
        List<Task> allTaskSorted = taskRepository.getAllSortBy(sort, boardId);

        // If the board is public and no filter is applied, return all tasks sorted
        if (visibility == Visibility.PUBLIC && filterStatuses == null) {
            return listMapper.mapList(allTaskSorted, GeneralTaskDTO.class);
        }

        // If the board is public with filters applied, return filtered tasks
        if (visibility == Visibility.PUBLIC) {
            return listMapper.mapList(taskRepository.findByStatusId(sort, filterStatuses, boardId), GeneralTaskDTO.class);
        }

        // Extract the token if prefixed with "Bearer"
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
        boolean isCollaborator = isCollaborator(jwtToken, boardId);

        if (visibility == Visibility.PRIVATE && !isOwner && !isCollaborator) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        if (isPendingAndNotOwner(jwtToken, boardId, board.getUserOid())) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }


        if (filterStatuses == null) {
            return listMapper.mapList(allTaskSorted, GeneralTaskDTO.class);
            // Check if the user is pending and not the owner
        }

        return listMapper.mapList(taskRepository.findByStatusId(sort, filterStatuses, boardId), GeneralTaskDTO.class);
        }

        public Task getTaskById (Integer taskId, String boardId, String accessToken){
            // Fetch the board by ID
            Board board = boardRepository.findById(boardId).orElseThrow(
                    () -> new ItemNotFoundException("Board not found with BOARD ID: " + boardId)
            );

            taskRepository.findById(taskId).orElseThrow(
                    () -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));

            // Check the board's visibility
            Visibility visibility = board.getVisibility();
            // If the board is public, you can return the task without needing a token
            if (visibility == Visibility.PUBLIC) {
                return taskRepository.findByIdAndBoardId(taskId, boardId);
            }

            // Extract the JWT token if present
            String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            boolean isOwner = isBoardOwner(board.getUserOid(), jwtToken);
            boolean isCollaborator = isCollaborator(jwtToken, boardId);


            // Check access for private boards
            if (visibility == Visibility.PRIVATE && !isOwner && !isCollaborator) {
                throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
            }

            if (isPendingAndNotOwner(jwtToken, boardId, board.getUserOid())) {
                throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
            }


            // If the board is private and the user has access, fetch the task
            return taskRepository.findByIdAndBoardId(taskId, boardId);
        }

        @Transactional
        public GeneralTaskDTO insertNewTask (NewTaskDTO newTask, String boardId, String jwtToken){

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
                throw new ForbiddenException("Only board owner and collaborators with write access can add tasks.");
            }

            if (!isOwner && !isCollaboratorWrite) {
                throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
            }


            if (newTask == null) {
                throw new BadRequestException("Invalid task data.");
            }

            if (jwtToken == null || jwtToken.trim().isEmpty()) {
                throw new AuthenticationException("JWT token is required.") {
                };
            }

            Status status = statusRepository.findById(newTask.getStatusId())
                    .orElseThrow(() -> new BadRequestException("status does not exist"));

            if (board.getLimitMaximumTask() && !"No Status".equals(status.getName())
                    && !"Done".equals(status.getName())) {
                int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
                if (noOfTasks >= board.getMaximumTask()) {
                    throw new LimitationException("the status has reached the limit");
                }
            }

            Task task = modelMapper.map(newTask, Task.class);
            task.setStatus(status);
            task.setBoard(board);
            Task insertedTask = taskRepository.save(task);
            return modelMapper.map(insertedTask, GeneralTaskDTO.class);

        }

        @Transactional
        public NewTaskDTO updateTask (Integer taskId, NewTaskDTO inputTask, String boardId, String jwtToken){
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
            Status status = statusRepository.findById(inputTask.getStatusId())
                    .orElseThrow(() -> new BadRequestException("Status does not exist"));

            if (board.getLimitMaximumTask() && !"No Status".equals(status.getName())
                    && !"Done".equals(status.getName())) {
                int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
                if (noOfTasks >= board.getMaximumTask()) {
                    throw new LimitationException("The status has reached the limit");
                }
            }

            Task existingTask = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ItemNotFoundException("Task not found with TASK ID: " + taskId));
            existingTask.setTitle(inputTask.getTitle());
            existingTask.setDescription(inputTask.getDescription());
            existingTask.setAssignees(inputTask.getAssignees());
            existingTask.setStatus(status);
            existingTask.setBoard(board);

            NewTaskDTO newTaskDTO = new NewTaskDTO();
            newTaskDTO.setId(existingTask.getId());
            newTaskDTO.setTitle(existingTask.getTitle());
            newTaskDTO.setDescription(existingTask.getDescription());
            newTaskDTO.setAssignees(existingTask.getAssignees());
            newTaskDTO.setStatusId(existingTask.getStatus().getId());
            newTaskDTO.setStatusName(status.getName());
            return newTaskDTO;
        }

        @Transactional
        public TaskDTO removeTask (Integer taskId, String boardId, String jwtToken){
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
                throw new ForbiddenException("Only board owner and collaborators with write access can delete tasks.");
            }

            if (!isOwner && !isCollaboratorWrite) {
                throw new ForbiddenException(boardId + " this board id is private. Only board owner can collaborator can access");
            }

            boolean isExistingTask = taskRepository.existsById(taskId);
            if (!isExistingTask) {
                throw new ItemNotFoundException("Task not found with TASK ID: " + taskId);
            }


            Task task = taskRepository.findByIdAndBoardId(taskId, boardId);

            for (TaskFile file : task.getFiles()) {
                fileRepository.delete(file);
                try {
                    // Define the path of the file in the storage location
                    Path targetLocation = this.fileStorageLocation.resolve(file.getFileName()); // Assuming `file.getFileName()` gives the file name
                    Files.delete(targetLocation);
                } catch (IOException e) {
                    e.printStackTrace(); // Log the exception
                    throw new RuntimeException("Error deleting file from filesystem", e);
                }
            }


            TaskDTO deletedTask = modelMapper.map(task, TaskDTO.class);
            taskRepository.delete(task);
            return deletedTask;
        }


        // Check if user is the board owner
        private boolean isBoardOwner (String userOid, String jwtToken){
            JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
            User user = userService.getUserByOid(userOid);
            return user.getOid().equals(jwtPayload.getOid());
        }

        // Check if collaborator has write access
        public boolean isCollaboratorWriteAccess (String jwtToken, String boardId){
            JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
            CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());
            System.out.println(collaborator);
            return collaborator != null && collaborator.getAccessRight() == AccessRight.WRITE && collaborator.getStatus() == ssi1.integrated.project_board.collab_management.Status.ACTIVE;
        }

        public boolean isPendingAndNotOwner (String jwtToken, String boardId, String userOid){
            // Extract the user's information from the JWT token
            JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);

            // Fetch the user based on the provided userOid
            User user = userService.getUserByOid(userOid);

            // Check if the user is the owner
            if (user.getOid().equals(jwtPayload.getOid())) {
                return false; // User is the owner, not pending
            }

            // Fetch the collaborator record for the given board and user from the token
            CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());

            // Debugging prints (can be removed later)
            System.out.println("Collaborator Status: " + (collaborator != null ? collaborator.getStatus() : "null"));
            System.out.println("User Oid: " + user.getOid());
            System.out.println("JwtPayload Oid: " + jwtPayload.getOid());

            // Check if collaborator exists and is in a "PENDING" state
            return collaborator != null
                    && collaborator.getStatus() == ssi1.integrated.project_board.collab_management.Status.PENDING
                    && !user.getOid().equals(jwtPayload.getOid());
        }
        

    public boolean isCollaborator (String jwtToken, String boardId){
        JwtPayload jwtPayload = jwtService.extractPayload(jwtToken);
        CollabBoard collaborator = collabBoardRepository.findByBoard_IdAndUser_Oid(boardId, jwtPayload.getOid());
        return collaborator != null;
    }
}
