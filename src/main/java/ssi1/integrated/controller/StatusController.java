package ssi1.integrated.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.EditLimitDTO;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.statusSetting.StatusSetting;
import ssi1.integrated.project_board.statusSetting.StatusSettingRepository;
import ssi1.integrated.services.BoardService;
import ssi1.integrated.services.StatusService;
import org.springframework.http.HttpStatus;
import ssi1.integrated.services.StatusSettingService;

import java.util.List;
import java.util.Optional;

@RestController

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/statuses")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private BoardService boardService;




    @GetMapping("/{boardId}/status")
    public List<Status> getAllStatus(){
        return  statusService.getAllStatus();
    }



//    @GetMapping("/{statusSettingId}/maximum-task")
//    public Optional<StatusSetting> getStatusSetting(@PathVariable Integer statusSettingId){
//        return statusSettingService.getStatusSettingById(statusSettingId);
//    }
//
//    @PatchMapping("/{statusSettingId}/maximum-task")
//    public StatusSetting updateStatusSetting(@PathVariable Integer statusSettingId,@RequestBody(required = false)  EditLimitDTO updateStatusSetting) {
//
//        return statusSettingService.updateStatusSetting(statusSettingId, updateStatusSetting);
//    }
//
//    @GetMapping("/{statusId}")
//    public Status getStatusById(@PathVariable Integer statusId){
//        return statusService.getStatusById(statusId);
//    }
//
//    @PutMapping("/{statusId}")
//    public ResponseEntity<NewStatusDTO> updateStatus(@PathVariable Integer statusId,@Valid @RequestBody NewStatusDTO updateStatus) {
//        return ResponseEntity.ok(statusService.updateStatus(statusId, updateStatus));
//    }
//
//    @PostMapping("")
//    public ResponseEntity<NewStatusDTO> addStatus(@Valid @RequestBody NewStatusDTO newStatusDTO){
//        NewStatusDTO createdStatus = statusService.insertNewStatus(newStatusDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
//    }

//    @DeleteMapping("/{statusId}")
//    public Status deleteStatus(@PathVariable Integer statusId){
//        return statusService.deleteStatus(statusId);
//    }
//
//    @DeleteMapping("/{statusId}/{newStatusId}")
//    public ResponseEntity<Status> transfer(@PathVariable Integer statusId,  @PathVariable Integer newStatusId) {
//
//        return ResponseEntity.ok(statusService.transferStatus(statusId,newStatusId));
//    }


}
