package ssi1.integrated.services;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.NewStatusDTO;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.ItemNotFoundException;

import java.util.List;
import ssi1.integrated.repositories.StatusRepository;
import ssi1.integrated.repositories.TaskRepository;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus() {
        return statusRepository.findAll();

    }

    public Status getStatusById(Integer statusId){
        return statusRepository.findById(statusId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );

    }

    @Transactional
    public Status updateStatus(Integer statusId, Status newStatus) {
        Status toBeUpdateStatus = statusRepository.findById(statusId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        toBeUpdateStatus.setName(newStatus.getName());
        toBeUpdateStatus.setDescription(newStatus.getDescription());
        if (newStatus.getStatusColor() == null || newStatus.getStatusColor().isEmpty()) {
            toBeUpdateStatus.setStatusColor("#CCCCCC");
        } else toBeUpdateStatus.setStatusColor(newStatus.getStatusColor());
        Status updatedStatus = statusRepository.save(toBeUpdateStatus);
//        return modelMapper.map(updatedStatus, StatusDTO.class);
        return updatedStatus;
    }


    @Transactional
    public NewStatusDTO insertNewStatus(NewStatusDTO newStatusDTO) {
        Status status = modelMapper.map(newStatusDTO, Status.class);
        System.out.println(status);
        Status insertedStatus = statusRepository.save(status);
        NewStatusDTO mappedStatus = modelMapper.map(insertedStatus, NewStatusDTO.class);
        return mappedStatus;
    }

//    @Transactional
//    public StatusDTO addStatus(Status newStatus){
//        Status existingStatus = statusRepository.findByName(newStatus.getName());
//        Status addedStatus =statusRepository.save(existingStatus);
//        StatusDTO newStatusDTO = modelMapper.map(addedStatus,StatusDTO.class);
//        return newStatusDTO;
//    }

    @Transactional
    public Status deleteStatus(Integer statusId){
        Status existingStatus=statusRepository.findById(statusId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );
        statusRepository.delete(existingStatus);
        return existingStatus;

    }

    @Transactional
    public Status transferStatus(Integer statusId,Integer newStatusId){
        Status newStatus=statusRepository.findById(newStatusId).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );
        List<Task> tasks=taskRepository.findByStatusId(statusId);
        for (Task task:tasks){
            task.setStatus(newStatus);
        }
        statusRepository.deleteById(statusId);
        return newStatus;

    }

}
