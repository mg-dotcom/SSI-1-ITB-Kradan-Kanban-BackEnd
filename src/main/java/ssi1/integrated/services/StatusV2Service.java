package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.status.StatusRepository;

import java.util.List;

@Service
public class StatusV2Service {
    @Autowired
    private StatusRepository statusRepository;


    public List<Status> getAllStatus(){
        return statusRepository.findAll();
    }
}
