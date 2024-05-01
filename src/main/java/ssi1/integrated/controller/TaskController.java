package ssi1.integrated.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.AddTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Task;
import ssi1.integrated.services.TaskService;


import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/v1/tasks")
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

    @PostMapping("")
    public ResponseEntity<AddTaskDTO> addTask(@RequestBody Task newTask){
        return ResponseEntity.ok(service.addTask(newTask));
    }

    @DeleteMapping("{taskId}")
    public ResponseEntity<TaskDTO>deleteTask(@PathVariable Integer taskId){
        return ResponseEntity.ok(service.deleteTask(taskId));
    }

    @PutMapping("{taskId}")
    public ResponseEntity<TaskDTO>updateTask(@PathVariable Integer taskId){
        return ResponseEntity.ok(service.updateTask(taskId));
    }
}
