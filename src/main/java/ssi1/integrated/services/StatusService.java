package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.entities.Status;
import ssi1.integrated.repositories.StatusRepository;

import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus(){
        return statusRepository.findAll();
    }

    @Transactional
    public Status addStatus(Status newStatus){
        Status existingStatus = statusRepository.findByName(newStatus.getName());
        return statusRepository.save(newStatus);
    }


}
