package com.example.fluxeip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender emailSender;
	
	public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("FluxEIP改密碼");
        message.setText("修改密碼, 點擊以下網址: "
                + "http://localhost:5173/reset-password?token=" + token);
        emailSender.send(message);
    }

}
