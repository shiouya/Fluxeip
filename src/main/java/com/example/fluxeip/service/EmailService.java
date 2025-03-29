package com.example.fluxeip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
	
	public void sendNewEmployee(String to,Integer id) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
        
        // 使用MimeMessageHelper來設置郵件的各個參數
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("入取通知");

        // 設定HTML格式的郵件內容
        String htmlContent = "<html><body>"
                + "<h2 style='color: #4CAF50;'>歡迎加入我們公司！</h2>"
                + "<p>您的會員ID(帳號): <strong>" + id + "</strong></p>"
                + "<p>您的初始密碼是：<strong>1234</strong></p>"
                + "<p>登入網址：<a href='http://localhost:5173/'>點此登入</a></p>"
                + "<p>祝您工作愉快！</p>"
                + "</body></html>";

        helper.setText(htmlContent, true); // 設置為HTML格式

        // 發送郵件
        emailSender.send(message);
		
		
//		SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("入取通知");
//        message.setText("歡迎進到我們公司，您的會員ID(帳號): "+id
//                + "您的密碼: 1234 登入網址:http://localhost:5173/");
//        emailSender.send(message);
	}

}
