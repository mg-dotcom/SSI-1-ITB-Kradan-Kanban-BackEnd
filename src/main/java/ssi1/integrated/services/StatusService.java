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
import ssi1.integrated.entities.Task;
import ssi1.integrated.exception.ItemNotFoundException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ListMapper listMapper;




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

        List<Task> tasks = taskRepository.findByStatusId(statusId);
        for (Task task:tasks){
            task.setStatus(newStatus);
        }

        statusRepository.deleteById(statusId);
        return newStatus;
    }

    @Transactional
    public List<LimitStatusDTO> updateAllStatusWithLimit(EditLimitDTO editLimitDTO) {
        List<Status> statusList = statusRepository.findAll();
        for (Status status : statusList) {
            modelMapper.map(editLimitDTO, status);
        }
        statusRepository.saveAll(statusList);
        return listMapper.mapList(statusList, LimitStatusDTO.class);
    }


    @Transactional
    public LimitStatusDTO updateStatusWithLimit(Integer statusId, LimitStatusDTO limitStatusDTO) {
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found with ID: " + statusId));

        int maximumTask = status.getMaximumTask();
        int noOfTask = status.getTasks().size();
        limitStatusDTO.setMaximumTask(maximumTask);
        List<Task> tasks = taskRepository.findByStatusId(statusId);

        // Cant patch by > maximumTask
        if (noOfTask > maximumTask) {
            status.setLimitMaximumTask(false);
            status = statusRepository.save(status);
            return modelMapper.map(status, LimitStatusDTO.class);
        }

        if (statusId == 1 || statusId == 4) {
          status.setMaximumTask(null);
          status.setLimitMaximumTask(false);
        }

       // by new name < maximumTask
        LimitStatusDTO updatedStatus = new LimitStatusDTO();
        updatedStatus.setId(limitStatusDTO.getId());
        updatedStatus.setName(limitStatusDTO.getName());
        updatedStatus.setDescription(limitStatusDTO.getDescription());
        updatedStatus.setLimitMaximumTask(limitStatusDTO.getLimitMaximumTask());
        updatedStatus.setNoOfTasks(tasks.size()); // Set the number of tasks
        updatedStatus.setMaximumTask(status.getMaximumTask());
        updatedStatus.setStatusColor(status.getStatusColor());
        Status statusData =  modelMapper.map(updatedStatus,Status.class);
        for (Task task : tasks) {
            task.setStatus(statusData);
        }
        statusRepository.save(statusData);

        // by existing name < maximumTask


        return updatedStatus;
    }
}
