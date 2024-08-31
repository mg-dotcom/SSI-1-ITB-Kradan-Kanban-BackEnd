package ssi1.integrated.services;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.*;
import ssi1.integrated.project_board.status.Status;
import ssi1.integrated.project_board.statusSetting.StatusSetting;
import ssi1.integrated.project_board.task.Task;
import ssi1.integrated.exception.handler.BadRequestException;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import java.util.List;

import ssi1.integrated.exception.handler.LimitationException;

import ssi1.integrated.project_board.status.StatusRepository;
import ssi1.integrated.project_board.task.TaskRepository;


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
    public NewStatusDTO updateStatus(Integer statusId, NewStatusDTO updateStatusDTO) {
        Status status = getStatusById(statusId);
        if (status.getName() == updateStatusDTO.getName()){
            throw new BadRequestException("Status name must be unique");
        }
        if (statusId.equals(1) || statusId.equals(7)) {
            throw new BadRequestException(status.getName() + " cannot be modified.");
        }

        if (updateStatusDTO.getStatusColor() == null || updateStatusDTO.getStatusColor().isEmpty()) {
            status.setStatusColor("#CCCCCC");
        } else {
            status.setStatusColor(updateStatusDTO.getStatusColor());
        }

        status.setName(updateStatusDTO.getName());
        status.setDescription(updateStatusDTO.getDescription());
        status.setStatusColor(updateStatusDTO.getStatusColor());

        Status updatedStatus = statusRepository.save(status);
        NewStatusDTO mappedStatus = modelMapper.map(updatedStatus, NewStatusDTO.class);
        return mappedStatus;
    }

    @Transactional
    public NewStatusDTO insertNewStatus(NewStatusDTO newStatusDTO) {
        if (statusRepository.existsByName(newStatusDTO.getName())) {
            throw new BadRequestException("Status name must be unique");
        }
        Status status = modelMapper.map(newStatusDTO, Status.class);
        Status insertedStatus = statusRepository.save(status);
        NewStatusDTO mappedStatus = modelMapper.map(insertedStatus, NewStatusDTO.class);
        return mappedStatus;
    }

    @Transactional
    public Status deleteStatus(Integer statusId){
        Status status = getStatusById(statusId);

        if (statusId.equals(1) || statusId.equals(7)) {
            throw new BadRequestException(status.getName() + " cannot be modified.");
        }

        if(status.getTasks().size() > 0){
            transferStatus(statusId,null);
        }

        statusRepository.delete(status);
        return status;
    }

    @Transactional
    public Status transferStatus(Integer statusId, Integer newStatusId) {
        Status newStatus = statusRepository.findById(newStatusId).orElseThrow(
                () -> new BadRequestException("The specified status for task transfer does not exist"));

        if (newStatusId != null) {
            if (statusId.equals(newStatusId)) {
                throw new BadRequestException("Destination status for task transfer must be different from current status.");
            }

            StatusSetting statusSetting = statusSettingService.getStatusSettingById(1).orElseThrow(
                    () -> new ItemNotFoundException("NOT FOUND"));

            if (statusSetting.getLimitMaximumTask()) {
                int noOfOldTasks = taskRepository.findByStatusId(statusId).size();
                int noOfNewTasks = taskRepository.findByStatusId(newStatusId).size();

                if (noOfOldTasks + noOfNewTasks > statusSetting.getMaximumTask()) {
                    throw new LimitationException("The destination status cannot be over limit after transfer.");
                }
            }

            List<Task> tasks = taskRepository.findByStatusId(statusId);
            for (Task task : tasks) {
                task.setStatus(newStatus);
            }
            taskRepository.saveAll(tasks);
            statusRepository.deleteById(statusId);
        } else {
            throw new BadRequestException("Destination status for task transfer not specified.");
        }

        return newStatus;
    }

}
