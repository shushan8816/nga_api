package com.nga.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Async
    public void sendSimpleMessage(String emailAddress, String verificationCode, String messageText) {
        String subject = "Account verification";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            String content = "<html><body>" + messageText + " " + verificationCode + "</html></body>";

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(emailAddress);
            helper.setFrom(username);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}