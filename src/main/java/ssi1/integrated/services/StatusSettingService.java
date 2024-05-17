package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.entities.StatusSetting;
import ssi1.integrated.exception.ItemNotFoundException;
import ssi1.integrated.repositories.StatusRepository;
import ssi1.integrated.repositories.StatusSettingRepository;

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
}
