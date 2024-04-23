package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssi1.integrated.entities.Task;
import ssi1.integrated.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<Task>getAllTasks(){
        return service.getAllTasks();
    }

    @GetMapping("{taskId}")
    public Task getAllTasks(@PathVariable Integer taskId){
        return service.getTask(taskId);
    }
}
