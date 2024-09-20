package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.services.TaskV2Service;

import java.util.List;
@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v2")
public class TaskV2Controller {
    @Autowired
    private TaskV2Service service;

    @GetMapping("/tasks")
    public List<Task> getAllTasks(){
        return service.getAllTask();
    }

}
