package ssi1.integrated.services;
import jakarta.transaction.Transactional;
import org.apache.el.util.ReflectionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ssi1.integrated.dtos.*;
import ssi1.integrated.entities.Status;
import ssi1.integrated.entities.StatusSetting;
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.ItemNotFoundException;
import java.util.List;

import ssi1.integrated.exception.LimitationException;
import ssi1.integrated.repositories.StatusRepository;
import ssi1.integrated.repositories.TaskRepository;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StatusSettingService statusSettingService;
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
        if (statusId.equals(1)||newStatus.getName() == null || newStatus.getName().isBlank()) {
            throw new IllegalArgumentException("Updating status with ID 1 is not allowed");
        }
        Status toBeUpdateStatus = statusRepository.findById(statusId).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        toBeUpdateStatus.setName(newStatus.getName());
        toBeUpdateStatus.setDescription(newStatus.getDescription());
        if (newStatus.getStatusColor() == null || newStatus.getStatusColor().isEmpty()) {
            toBeUpdateStatus.setStatusColor("#CCCCCC");
        } else toBeUpdateStatus.setStatusColor(newStatus.getStatusColor());
        Status updatedStatus = statusRepository.save(toBeUpdateStatus);
        return updatedStatus;
    }


    @Transactional
    public NewStatusDTO insertNewStatus(NewStatusDTO newStatusDTO) {
        Status status = modelMapper.map(newStatusDTO, Status.class);

        Status insertedStatus = statusRepository.save(status);
        NewStatusDTO mappedStatus = modelMapper.map(insertedStatus, NewStatusDTO.class);
        return mappedStatus;
    }

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
        StatusSetting statusSetting = statusSettingService.getStatusSettingById(1).orElseThrow(
                ()->new ItemNotFoundException("NOT FOUND")
        );

        if (statusSetting.getLimitMaximumTask()) {
            int noOfOldTasks = taskRepository.findByStatusId(statusId).size();
            int noOfNewTasks=taskRepository.findByStatusId(newStatusId).size();
            
            if (noOfOldTasks + noOfNewTasks > statusSetting.getMaximumTask()) {
                throw new LimitationException("the destination status cannot be over limit after transfer");
            }
        }

        List<Task> tasks = taskRepository.findByStatusId(statusId);
        for (Task task:tasks){
            task.setStatus(newStatus);
        }

        statusRepository.deleteById(statusId);
        return newStatus;
    }

}
