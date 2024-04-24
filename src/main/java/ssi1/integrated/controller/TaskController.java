package ssi1.integrated.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
//    @Autowired
//    private ModelMapper modelMapper;

    @GetMapping("")
    public List<TaskDTO>getAllTasks(){
        return service.getAllTasks();
    }

    @GetMapping("{taskId}")
    public Task getAllTasks(@PathVariable Integer taskId){
        return service.getTask(taskId);
    }
}
