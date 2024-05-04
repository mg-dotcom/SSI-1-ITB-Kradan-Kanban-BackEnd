package ssi1.integrated.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.NewTaskDTO;
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

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer taskId){
        return ResponseEntity.ok(service.getTaskById(taskId));
    }


    @PostMapping("")
    public ResponseEntity<NewTaskDTO> addTask(@RequestBody NewTaskDTO newTaskDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insertNewTask(newTaskDTO));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDTO> deleteTask(@PathVariable Integer taskId) {
       return ResponseEntity.ok(service.removeTask(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<NewTaskDTO> updateTask(@PathVariable Integer taskId,@RequestBody NewTaskDTO newTaskDTO){
        return ResponseEntity.ok(service.updateTask(taskId,newTaskDTO));
    }
}
