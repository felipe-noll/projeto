package com.example.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailSimples(String para, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sistema@projeto.com");
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(texto);
        mailSender.send(message);
    }
}
