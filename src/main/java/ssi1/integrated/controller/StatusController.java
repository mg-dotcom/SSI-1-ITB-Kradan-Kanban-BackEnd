package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.NewTaskDTO;
import ssi1.integrated.dtos.StatusDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.services.StatusService;

import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
public class StatusController {
    @Autowired
    public StatusService statusService;

    @GetMapping("")
    public List<StatusDTO> getAllStatus(){
        return statusService.getAllStatus();
    }

    @PostMapping("")
    public ResponseEntity<Status> addStatus(@RequestBody Status status){
        Status createdStatus = statusService.addStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @DeleteMapping("/{statusId}")
    public Status deleteStatus(@PathVariable Integer statusId){
        return statusService.deleteStatus(statusId);

    }

    @GetMapping("/{statusId}")
    public Status transfer(@PathVariable Integer statusId){
        return statusService.deleteStatus(statusId);

    }


}
