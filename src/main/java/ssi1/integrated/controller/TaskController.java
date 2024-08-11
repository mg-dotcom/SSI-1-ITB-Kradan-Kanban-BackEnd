package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.GeneralTaskDTO;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.Task;
import ssi1.integrated.services.TaskService;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v2/tasks")

public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<TaskDTO>getAllTasks(
            @RequestParam(required = false,defaultValue = "createdOn") String sortBy,
            @RequestParam(required = false) List<String> filterStatuses,
            @RequestParam(required = false,defaultValue = "asc") String direction
    ){
        return service.getAllTasks(sortBy, filterStatuses, direction);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer taskId){
        return ResponseEntity.ok(service.getTaskById(taskId));
    }

    @PostMapping("")
    public ResponseEntity<GeneralTaskDTO> addTask(@Valid @RequestBody NewTaskDTO newTaskDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.insertNewTask(newTaskDTO));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDTO> deleteTask(@PathVariable Integer taskId) {
       return ResponseEntity.ok(service.removeTask(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<NewTaskDTO> updateTask(@Valid @PathVariable Integer taskId,@Valid @RequestBody NewTaskDTO newTaskDTO){
        return ResponseEntity.ok(service.updateTask(taskId,newTaskDTO));
    }

}
