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

    public void sendEmail(String boardId,String to,String boardOwner,String accessRight,String boardName) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(boardOwner + " has invited you to collaborate with"+" "+ accessRight + " access right on "+"'"+boardName+"'");
        message.setText(boardOwner + " has invited you to collaborate with"+" "+ accessRight + " access right on "+"'"+boardName+"'."+ "You can accept or decline this invitation at "+"http://localhost:8080/v3/boards/"+boardId+"/collabs/invitations");

        mailSender.send(message);
        System.out.println("Sent mail successfully!");
    }
}