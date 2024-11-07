package ssi1.integrated.services;

import org.springframework.security.core.AuthenticationException;
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

    public void sendEmail(String boardId,String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Board Inviting!!");
        message.setText(boardId);

        mailSender.send(message);
        System.out.println("Sent mail successfully!");
    }
}