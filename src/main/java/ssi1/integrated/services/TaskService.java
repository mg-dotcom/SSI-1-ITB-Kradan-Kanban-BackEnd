package ssi1.integrated.services;

import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ssi1.integrated.entities.Task;
import ssi1.integrated.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Task getTask(Integer taskId){
        return taskRepository.findById(taskId).orElseThrow(
                ()->new HttpClientErrorException(HttpStatus.NOT_FOUND,"Task Id "+taskId+" DOES NOT EXIST!!!")
        );
    }

}
