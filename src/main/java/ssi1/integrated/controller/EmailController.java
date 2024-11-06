package ssi1.integrated.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ssi1.integrated.dtos.SendEmailDTO;
import ssi1.integrated.services.EmailService;

@RestController
@RequestMapping("/v3")
public class EmailController {
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestHeader(name = "Authorization") String accessToken,@RequestBody SendEmailDTO sendEmailDTO) {
        emailService.sendEmail(accessToken,sendEmailDTO);
        return "Email sent successfully!";
    }
}

