package com.amar.emailservice.consumer;

import com.amar.emailservice.dtos.SendEmailEventDto;
import com.amar.emailservice.util.EmailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailConsumer {

    private ObjectMapper objectMapper;

    public SendEmailConsumer(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "SEND_EMAIL", groupId = "emailService")
    public void handleSendEmailEvent(String message){
        try {
            SendEmailEventDto emailEventDto = objectMapper.readValue(message, SendEmailEventDto.class);

            String fromEmail = emailEventDto.getFrom();
            String toEmail = emailEventDto.getTo();
            String body = emailEventDto.getBody();
            String subject = emailEventDto.getSubject();

            System.out.println("TLSEmail Start");
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("amaremailservice@gmail.com", "tlnx hryv aqbg xygz");
                }
            };
            Session session = Session.getInstance(props, auth);

            EmailUtil.sendEmail(session, toEmail,subject, body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
