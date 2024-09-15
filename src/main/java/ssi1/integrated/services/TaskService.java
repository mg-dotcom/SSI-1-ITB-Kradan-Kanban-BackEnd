package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ssi1.integrated.configs.ListMapper;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.project_board.board.Board;
import ssi1.integrated.project_board.board.BoardRepository;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.exception.handler.LimitationException;
import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.TaskRepository;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusService statusService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<GeneralTaskDTO> getAllTasks(String sortBy, List<String> filterStatuses, String direction, String boardId) {

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


    public Task getTaskById(Integer taskId, String boardId) {
        Task task = taskRepository.findByStatusIdAndBoardId(taskId, boardId);
        task.setStatus(task.getStatus());
        return task;

    }

    @Transactional
    public GeneralTaskDTO insertNewTask(NewTaskDTO newTask, String boardId) {

        Status status = statusRepository.findById(newTask.getStatus())
                .orElseThrow(() -> new BadRequestException("status does not exist"));

        Optional<Board> board = boardRepository.findById(boardId);
        if (board.get().getLimitMaximumTask() && !"No Status".equals(status.getName())
                && !"Done".equals(status.getName())) {
            int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
            if (noOfTasks >= board.get().getMaximumTask()) {
                throw new LimitationException("the status has reached the limit");
            }
        }

        Task task = modelMapper.map(newTask, Task.class);
        task.setStatus(status);
        task.setBoard(board.get());
        Task insertedTask = taskRepository.save(task);
        return modelMapper.map(insertedTask, GeneralTaskDTO.class);

    }

    @Transactional
    public NewTaskDTO updateTask(Integer taskId, NewTaskDTO inputTask, String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board with id " + boardId + " not found"));

        boolean isExistingTask = taskRepository.existsById(taskId);
        if (!isExistingTask) {
            throw new ItemNotFoundException("NOT FOUND");
        }
        Status status = statusRepository.findById(inputTask.getStatus())
                .orElseThrow(() -> new BadRequestException("status does not exist"));

        if (board.getLimitMaximumTask() && !"No Status".equals(status.getName())
                && !"Done".equals(status.getName())) {
            int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
            if (noOfTasks >= board.getMaximumTask()) {
                throw new LimitationException("the status has reached the limit");
            }
        }
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Task with id " + taskId + " not found"));
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
        return newTaskDTO;
    }

    @Transactional
    public TaskDTO removeTask(Integer taskId,String boardId) {
        boolean isExistingTask = taskRepository.existsById(taskId);
        if (!isExistingTask) {
            throw new ItemNotFoundException("NOT FOUND");
        }
        Task task = taskRepository.findByIdAndBoardId(taskId,boardId);
        TaskDTO deletedTask = modelMapper.map(task, TaskDTO.class);
        taskRepository.delete(task);
        return deletedTask;
    }

}
