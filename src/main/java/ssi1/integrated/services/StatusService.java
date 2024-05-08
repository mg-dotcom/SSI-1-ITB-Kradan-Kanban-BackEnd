package ssi1.integrated.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssi1.integrated.repositories.StatusRepository;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    
}
