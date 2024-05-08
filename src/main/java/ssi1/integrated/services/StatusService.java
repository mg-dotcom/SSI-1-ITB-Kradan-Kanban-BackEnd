package ssi1.integrated.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.StatusDTO;
import ssi1.integrated.dtos.TaskDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.ItemNotFoundException;
import ssi1.integrated.repositories.StatusRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    public Status getStatusById(Integer taskId){
        return statusRepository.findById(taskId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );

    }

    @Transactional
    public StatusDTO updateStatus(Integer statusId, Status newStatus){
        Status toBeUpdateStatus = statusRepository.findById(statusId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        toBeUpdateStatus.setName(newStatus.getName());
        toBeUpdateStatus.setDescription(newStatus.getDescription());
        if (newStatus.getStatusColor() == null || newStatus.getStatusColor().isEmpty()){
            toBeUpdateStatus.setStatusColor("#CCCCCC");
        }else toBeUpdateStatus.setStatusColor(newStatus.getStatusColor());
        Status updatedStatus = statusRepository.save(toBeUpdateStatus);
        return modelMapper.map(updatedStatus,StatusDTO.class);
    }


}
