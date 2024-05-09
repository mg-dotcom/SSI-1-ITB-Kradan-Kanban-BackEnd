package ssi1.integrated.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.StatusDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.exception.ItemNotFoundException;
import ssi1.integrated.repositories.StatusRepository;
import ssi1.integrated.repositories.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<StatusDTO> getAllStatus(){
        return statusRepository.findAll().stream()
                .map(status -> modelMapper.map(status,StatusDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public Status addStatus(Status newStatus){
        Status existingStatus = statusRepository.findByName(newStatus.getName());
        return statusRepository.save(newStatus);
    }

    @Transactional
    public Status deleteStatus(Integer statusId){
        Status existingStatus=statusRepository.findById(statusId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );
        statusRepository.delete(existingStatus);
        return existingStatus;

    }




}
