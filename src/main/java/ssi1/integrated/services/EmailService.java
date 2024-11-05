package ssi1.integrated.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ssi1.integrated.dtos.SendEmailDTO;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(SendEmailDTO sendEmailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendEmailDTO.getTo());
        message.setSubject(sendEmailDTO.getSubject());
        message.setText(sendEmailDTO.getBody());

        mailSender.send(message);
    }
}