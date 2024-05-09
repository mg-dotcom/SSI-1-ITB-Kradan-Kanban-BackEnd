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
    private StatusService statusService;

    @GetMapping("")

    public List<Status> getAllStatus(){
        return statusService.getAllStatus();
    }



    @GetMapping("/{statusId}")
    public Status getStatusById(@PathVariable Integer statusId){
        return statusService.getStatusById(statusId);
    }

    @PutMapping("/{statusId}")

    public ResponseEntity<Status> updateStatus(@PathVariable Integer statusId, @RequestBody Status updateStatus){
        return ResponseEntity.ok(statusService.updateStatus(statusId,updateStatus));
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

    @DeleteMapping("/{statusId}/{newStatusId}")
    public ResponseEntity<Status>transfer(@PathVariable Integer statusId,@PathVariable Integer newStatusId){
        return ResponseEntity.ok(statusService.transferStatus(statusId,newStatusId));
    }

}
