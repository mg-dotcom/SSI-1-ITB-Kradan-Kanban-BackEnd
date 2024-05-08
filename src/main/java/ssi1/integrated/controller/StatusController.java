package ssi1.integrated.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi1.sit.kmutt.ac.th"})
@RequestMapping("/v2/statuses")
public class StatusController {
    
}
