package com.example.Car_Service3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void sendMail(String emailTo, String subject, String message, FileSystemResource resource)  throws MessagingException
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        boolean multipart = true;

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart);

        helper.setTo(emailTo);
        helper.setSubject(subject);

        helper.setText(message);

        helper.addAttachment("Details_Results.xls", resource);

        mailSender.send(mimeMessage);
    }
}
