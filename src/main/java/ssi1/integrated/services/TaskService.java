package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.AddTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.ItemNotFoundException;
import ssi1.integrated.repositories.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<TaskDTO> getAllTasks(){
        return taskRepository.findAll().stream()
                .map(task -> modelMapper.map(task,TaskDTO.class))
                .collect(Collectors.toList());
    }

    public Task getTaskById(Integer taskId){
        return taskRepository.findById(taskId).orElseThrow(
                ()->new ItemNotFoundException("Task Id "+taskId+" DOES NOT EXIST!!!")
        );
    }

    @Transactional
    public AddTaskDTO addTask(Task newTask){
        if (newTask.getStatus().isEmpty()){
            newTask.setStatus("NO_STATUS");
        }
        Task task = modelMapper.map(newTask, Task.class);
        return modelMapper.map(taskRepository.save(task), AddTaskDTO.class);
    }

    @Transactional
    public TaskDTO deleteTask(Integer taskId){
        Task existingTask=taskRepository.findById(taskId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );
        TaskDTO taskdto=modelMapper.map(existingTask,TaskDTO.class);
        taskRepository.deleteById(taskId);
        return taskdto;

    }

    @Transactional
    public TaskDTO updateTask(Integer taskId,Task editTask){
        Task existingTask=taskRepository.findById(taskId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );
        TaskDTO taskdto=modelMapper.map(existingTask,TaskDTO.class);
        taskRepository.save(editTask);
        return taskdto;
    }


}
