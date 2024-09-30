package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.configs.ListMapper;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.exception.handler.ForbiddenException;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.board.Visibility;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.exception.handler.LimitationException;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.TaskRepository;
import ssi1.integrated.security.JwtService;
import ssi1.integrated.user_account.User;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private BoardService boardService;
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

    public List<GeneralTaskDTO> getAllTasks(String sortBy, List<String> filterStatuses, String direction, String boardId, String jwtToken) {

        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);

        // Can't access board
        if (!authorizationResult.isOwner() && !authorizationResult.isPublic()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        Sort.Order sortOrder = new Sort.Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Sort sort = Sort.by(sortOrder);

        if (filterStatuses == null) {
            List<Task> allTaskSorted = taskRepository.getAllSortBy(sort, boardId);
            return listMapper.mapList(allTaskSorted, GeneralTaskDTO.class);
        }

        return listMapper.mapList(taskRepository.findByStatusId(sort, filterStatuses, boardId), GeneralTaskDTO.class);

    }


    public Task getTaskById(Integer taskId,String boardId, String jwtToken) {
        Task task=taskRepository.findById(taskId).orElseThrow(
                ()->new ItemNotFoundException("Task not found with TASK ID: " + taskId));

        BoardAuthorizationResult authorizationResult = authorizeBoardReadAccess(boardId, jwtToken);

        // Can't access board
        if (!authorizationResult.isOwner() && !authorizationResult.isPublic()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        return taskRepository.findByIdAndBoardId(task.getId(),boardId);
    }


    @Transactional
    public GeneralTaskDTO insertNewTask(NewTaskDTO newTask, String boardId,String jwtToken) {
        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token is required");
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        Status status = statusRepository.findById(newTask.getStatus())
                .orElseThrow(() -> new BadRequestException("status does not exist"));

        Board board = boardService.getBoardById(boardId);
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
    public NewTaskDTO updateTask(Integer taskId, NewTaskDTO inputTask, String boardId,String jwtToken) {
        Board board = boardService.getBoardById(boardId);
        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token is required");
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        boolean isExistingTask = taskRepository.existsById(taskId);

        if (!isExistingTask) {
            throw new ItemNotFoundException("Task not found with TASK ID: " + taskId);
        }
        Status status = statusRepository.findById(inputTask.getStatus())
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

        NewTaskDTO newTaskDTO=new NewTaskDTO();
        newTaskDTO.setId(existingTask.getId());
        newTaskDTO.setTitle(existingTask.getTitle());
        newTaskDTO.setDescription(existingTask.getDescription());
        newTaskDTO.setAssignees(existingTask.getAssignees());
        newTaskDTO.setStatus(existingTask.getStatus().getId());
        newTaskDTO.setStatusName(status.getName());
        return newTaskDTO;
    }

    @Transactional
    public TaskDTO removeTask(Integer taskId,String boardId, String jwtToken) {
        BoardAuthorizationResult authorizationResult  = authorizeBoardModifyAccess(boardId, jwtToken);

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token is required");
        }

        //Can't access board
        if (!authorizationResult.isOwner()) {
            throw new ForbiddenException("Access denied to board BOARD ID: " + boardId);
        }

        boolean isExistingTask = taskRepository.existsById(taskId);
        if (!isExistingTask) {
            throw new ItemNotFoundException("Task not found with TASK ID: " + taskId);
        }
        Task task = taskRepository.findByIdAndBoardId(taskId,boardId);
        TaskDTO deletedTask = modelMapper.map(task, TaskDTO.class);
        taskRepository.delete(task);
        return deletedTask;
    }

    public BoardAuthorizationResult authorizeBoardReadAccess(String boardId, String jwtToken) {
        Board board = boardService.getBoardById(boardId);

        // If the board is public, return immediately allowing access
        if (board.getVisibility() == Visibility.PUBLIC) {
            return new BoardAuthorizationResult(false, true);  // Public board, ownership doesn't matter
        }

        User user = userService.getUserByOid(board.getUserOid());

        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);

        // Private board means isPublic should be false
        return new BoardAuthorizationResult(isOwner, false);
    }

    public BoardAuthorizationResult authorizeBoardModifyAccess(String boardId, String jwtToken) {
        Board board = boardService.getBoardById(boardId);

        User user = userService.getUserByOid(board.getUserOid());
        Visibility visibilityByBoardId = boardRepository.findVisibilityByBoardId(boardId);
        String tokenUsername = jwtService.extractUsername(jwtToken);

        boolean isOwner = user.getUsername().equals(tokenUsername);
        boolean isPublic = (visibilityByBoardId == Visibility.PUBLIC);

        return new BoardAuthorizationResult(isOwner, isPublic);
    }
}
