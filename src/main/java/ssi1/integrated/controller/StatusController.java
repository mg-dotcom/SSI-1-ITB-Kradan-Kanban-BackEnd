package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ssi1.integrated.dtos.StatusDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.services.StatusService;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th"})
@RequestMapping("/v2/statuses")
public class StatusController {

    @Autowired
    private StatusService service;

    @GetMapping("")
    public List<StatusDTO> getAllStatus(){
        return service.getAllStatus();
    }

    @GetMapping("/")
    public List<Status> getAllStatusNotDTO(){
        return service.getAllStatusNotDTO();
    }

    @GetMapping("/{statusId}")
    public Status getStatusById(@PathVariable Integer statusId){
        return service.getStatusById(statusId);
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<StatusDTO> updateStatus(@PathVariable Integer statusId, @RequestBody Status updateStatus){
        return ResponseEntity.ok(service.updateStatus(statusId,updateStatus));
    }

    @PostMapping("")
    public ResponseEntity<StatusDTO> addStatus(@RequestBody Status status){
        StatusDTO createdStatus = service.addStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @DeleteMapping("/{statusId}")
    public Status deleteStatus(@PathVariable Integer statusId){
        return service.deleteStatus(statusId);
    }


}
