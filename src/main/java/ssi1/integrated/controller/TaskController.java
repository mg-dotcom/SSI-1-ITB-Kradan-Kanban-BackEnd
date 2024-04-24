package ssi1.integrated.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Task;
import ssi1.integrated.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/itb-kk/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<TaskDTO>getAllTasks(){
        return service.getAllTasks();
    }

    @GetMapping("{taskId}")
    public ResponseEntity<Task> getAllTasks(@PathVariable Integer taskId){
        return ResponseEntity.ok(service.getTask(taskId));
    }
}
