package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.ItemNotFoundException;
import ssi1.integrated.repositories.StatusRepository;
import ssi1.integrated.repositories.TaskRepository;
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


    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    public Task getTaskById(Integer taskId){
        return taskRepository.findById(taskId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );
    }


    @Transactional
    public NewTaskDTO insertNewTask(NewTaskDTO newTask) {
        Task task = modelMapper.map(newTask, Task.class);
        Status existingStatus = findStatusByName(newTask.getStatus().getName());
        task.setStatus(existingStatus);
        System.out.println(existingStatus);
        Task insertedTask = taskRepository.save(task);
        NewTaskDTO newTaskDTO = modelMapper.map(insertedTask, NewTaskDTO.class);
        return newTaskDTO;
    }


    @Transactional
    public NewTaskDTO updateTask(Integer taskId,NewTaskDTO updateTask){
        Task toBeUpdateTask = taskRepository.findById(taskId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        toBeUpdateTask.setTitle(updateTask.getTitle());
        toBeUpdateTask.setDescription(updateTask.getDescription());
        toBeUpdateTask.setAssignees(updateTask.getAssignees());

        toBeUpdateTask.setStatus(updateTask.getStatus());

        Task updatedTask = taskRepository.save(toBeUpdateTask);
        return modelMapper.map(updatedTask, NewTaskDTO.class);
    }
    @Transactional
    public TaskDTO removeTask(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        TaskDTO deletedTask = modelMapper.map(task,TaskDTO.class);
        taskRepository.delete(task);
        return deletedTask;
    }

    public Status findStatusByName(String statusName){
        Status status = statusRepository.findByName(statusName);
        if(status != null){
            return  status;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Status with name '" + statusName + "' not found");
    }

}
