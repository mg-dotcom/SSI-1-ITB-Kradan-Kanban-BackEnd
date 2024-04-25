package ssi1.integrated.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Task;
import ssi1.integrated.services.TaskService;

import java.util.List;

import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    TaskService service;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> findAll(){
        List<Task> tasks = service.findAll();
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findTaskById(@PathVariable Integer id){
        return ResponseEntity.ok(service.findById(id));
    }


}
