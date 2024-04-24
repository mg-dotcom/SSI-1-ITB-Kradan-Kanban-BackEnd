package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Task;
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

    public Task getTask(Integer taskId){
        return taskRepository.findById(taskId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Task Id "+taskId+" DOES NOT EXIST!!!")
        );
    }

}
