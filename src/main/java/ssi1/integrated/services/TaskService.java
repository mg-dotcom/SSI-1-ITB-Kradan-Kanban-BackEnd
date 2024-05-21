package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.StatusSetting;
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.BadRequestException;
import ssi1.integrated.exception.ItemNotFoundException;
import ssi1.integrated.exception.LimitationException;
import ssi1.integrated.repositories.StatusRepository;
import ssi1.integrated.repositories.StatusSettingRepository;
import ssi1.integrated.repositories.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusSettingRepository statusSettingRepository;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;

    public List<TaskDTO> getAllTasks(String sortBy, List<String> filterStatuses, String direction) {

        Sort.Order sortOrder = new Sort.Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Sort sort = Sort.by(sortOrder);

        if (filterStatuses == null) {
            return taskRepository.getAllBy(sort).stream()
                    .map(task -> convertTaskToTaskDTO(task))
                    .collect(Collectors.toList());
        }

        return taskRepository.findByStatusId(sort, filterStatuses).stream()
                .map(task -> convertTaskToTaskDTO(task))
                .collect(Collectors.toList());

    }

    private TaskDTO convertTaskToTaskDTO(Task task){
        String statusName = task.getStatus().getName();
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
        taskDTO.setStatus(statusName);
        return taskDTO;
    }
    public Task getTaskById(Integer taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
    }

    @Transactional
    public GeneralTaskDTO insertNewTask(NewTaskDTO newTask) {
        StatusSetting statusSetting = statusSettingRepository.findById(1).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND THIS KANBAN ID")
        );
        Status status = statusRepository.findById(newTask.getStatus())
                .orElseThrow(() -> new ItemNotFoundException("NOT FOUND"));
        if (statusSetting.getLimitMaximumTask()) {
            int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
            if (noOfTasks >= statusSetting.getMaximumTask()) {
                throw new LimitationException("the status has reached the limit");
            }
        }

        Task task = modelMapper.map(newTask, Task.class);
        task.setStatus(status);
        Task insertedTask = taskRepository.save(task);
        return modelMapper.map(insertedTask, GeneralTaskDTO.class);

    }

    @Transactional
    public NewTaskDTO updateTask(Integer taskId, NewTaskDTO inputTask) {
        StatusSetting statusSetting = statusSettingRepository.findById(1).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND THIS KANBAN ID")
        );
        Boolean isExistingTask = taskRepository.existsById(taskId);
        if (!isExistingTask) {
            throw new ItemNotFoundException("NOT FOUND");
        }
        Status status = statusService.getStatusById(inputTask.getStatus());
        if(statusSetting.getLimitMaximumTask()){
            int noOfTasks = taskRepository.findByStatusId(status.getId()).size();
            if (noOfTasks >= statusSetting.getMaximumTask()) {
                throw new LimitationException("the status has reached the limit");
            }
        }
        Task task = modelMapper.map(inputTask, Task.class);
        task.setStatus(status);
        task.setId(taskId);
        Task updatedTask = taskRepository.save(task);
        NewTaskDTO updatedTaskDTO = new NewTaskDTO();
        updatedTaskDTO.setId(updatedTask.getId());
        updatedTaskDTO.setDescription(updatedTask.getDescription());
        updatedTaskDTO.setTitle(updatedTask.getTitle());
        updatedTaskDTO.setAssignees(updatedTask.getAssignees());
        updatedTaskDTO.setStatus(updatedTask.getStatus().getId());
        return updatedTaskDTO;
    }

    @Transactional
    public TaskDTO removeTask(Integer taskId) {
        Boolean isExistingTask = taskRepository.existsById(taskId);
        if (!isExistingTask) {
            throw new ItemNotFoundException("NOT FOUND");
        }
        Task task = getTaskById(taskId);
        TaskDTO deletedTask = modelMapper.map(task, TaskDTO.class);
        taskRepository.delete(task);
        return deletedTask;
    }

}
