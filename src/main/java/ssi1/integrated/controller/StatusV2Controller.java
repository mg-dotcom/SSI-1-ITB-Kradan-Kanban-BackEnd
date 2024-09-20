package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.services.StatusV2Service;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v2")
public class StatusV2Controller {
    @Autowired
    private StatusV2Service service;

    @GetMapping("/statuses")
    public List<Status> getAllStatuses(){
        return service.getAllStatus();
    }
}
