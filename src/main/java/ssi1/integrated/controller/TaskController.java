package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.services.TaskService;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")

public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("/{boardId}/tasks")
    public List<TaskDTO> getAllTasks(@PathVariable String boardId){
        return service.getAllTask(boardId);
    }

//    @GetMapping("")
//    public List<Task>getAllTasks(
//            @RequestParam(required = false,defaultValue = "createdOn") String sortBy,
//            @RequestParam(required = false) List<String> filterStatuses,
//            @RequestParam(required = false,defaultValue = "asc") String direction
//    ){
//        return service.getAllTasks(sortBy, filterStatuses, direction);
//    }

//    @GetMapping("/{taskId}")
//    public ResponseEntity<Task> getTaskById(@PathVariable Integer taskId){
//        return ResponseEntity.ok(service.getTaskById(taskId));
//    }
//
//    @PostMapping("")
//    public ResponseEntity<GeneralTaskDTO> addTask(@Valid @RequestBody NewTaskDTO newTaskDTO){
//        return ResponseEntity.status(HttpStatus.CREATED).body(service.insertNewTask(newTaskDTO));
//    }
//
//    @DeleteMapping("/{taskId}")
//    public ResponseEntity<TaskDTO> deleteTask(@PathVariable Integer taskId) {
//       return ResponseEntity.ok(service.removeTask(taskId));
//    }
//
//    @PutMapping("/{taskId}")
//    public ResponseEntity<NewTaskDTO> updateTask(@Valid @PathVariable Integer taskId,@Valid @RequestBody NewTaskDTO newTaskDTO){
//        return ResponseEntity.ok(service.updateTask(taskId,newTaskDTO));
//    }

}
