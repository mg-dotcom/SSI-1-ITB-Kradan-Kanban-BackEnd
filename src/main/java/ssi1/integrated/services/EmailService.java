package ssi1.integrated.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import ssi1.integrated.dtos.SendEmailDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;


@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String NO_REPLY_SUFFIX = "noreply@intproj23.sit.kmutt.ac.th";
    @Value("spring.mail.username")
    private String fromEmailId;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String boardId,String to,String boardOwner,String accessRight,String boardName) throws MessagingException, UnsupportedEncodingException {

        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, false, "UTF-8");
//        message.setFrom("ITBKK-SSI1");
//        message.setTo(to);
//        message.setSubject(boardOwner + " has invited you to collaborate with"+" "+ accessRight + " access right on "+"'"+boardName+"'");
//        message.setText(boardOwner + " has invited you to collaborate with"+" "+ accessRight + " access right on "+"'"+boardName+"'."+ "You can accept or decline this invitation at "+"https://intproj23.sit.kmutt.ac.th/ssi1/board/"+boardId+"/collab/invitations");
//        String replyTo = "DO_NOT_REPLY_" + NO_REPLY_SUFFIX;
//        message.setReplyTo(replyTo);
        String replyTo = "DO_NOT_REPLY_" + NO_REPLY_SUFFIX;

        String fromDisplayName = "ITBKK-SSI1";
        helper.setFrom(fromEmailId, fromDisplayName);
        helper.setReplyTo(replyTo);
        helper.setTo(to);
        helper.setText(boardOwner + " has invited you to collaborate with"+" "+ accessRight + " access right on "+"'"+boardName+"'."+ "You can accept or decline this invitation at "+"https://intproj23.sit.kmutt.ac.th/ssi1/board/"+boardId+"/collab/invitations");
        helper.setSubject(boardOwner + " has invited you to collaborate with"+" "+ accessRight + " access right on "+"'"+boardName+"'");

        mailSender.send(mailMessage);
        System.out.println("Sent mail successfully!");
    }
}