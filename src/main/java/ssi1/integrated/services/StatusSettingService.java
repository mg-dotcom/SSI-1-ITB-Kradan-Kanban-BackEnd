package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.dtos.EditLimitDTO;
import ssi1.integrated.board.entities.StatusSetting;
import ssi1.integrated.exception.handler.ItemNotFoundException;
import ssi1.integrated.board.repositories.StatusSettingRepository;

import java.util.Optional;

@Service
public class StatusSettingService {
    @Autowired
    private StatusSettingRepository repository;

    public Optional<StatusSetting> getStatusSettingById(Integer statusSettingId){
        Boolean isExistingTask = repository.existsById(statusSettingId);
        if(!isExistingTask){
            throw new ItemNotFoundException("NOT FOUND");
        }
        return  repository.findById(statusSettingId);
    }
    public StatusSetting updateStatusSetting(Integer statusSettingId, EditLimitDTO editLimitDTO){
        StatusSetting statusSetting = repository.findById(statusSettingId).orElseThrow(() -> new ItemNotFoundException("NOT FOUND"));
        if(editLimitDTO == null ){
            statusSetting.setLimitMaximumTask(statusSetting.getLimitMaximumTask());
            repository.save(statusSetting);
            return statusSetting;
        }
        statusSetting.setId(statusSettingId);
        statusSetting.setLimitMaximumTask(editLimitDTO.getLimitMaximumTask());
        repository.save(statusSetting);
        return  statusSetting;
    }


}
