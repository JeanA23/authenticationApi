package com.authenticationsystem.apiauthentication.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = 
            "http://localhost:8080/api/v1/auth/reset-password?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setFrom("no-reply@authapi.com");
        mail.setSubject("Password reset");
        mail.setText(
            "Click on this link (valid for 24 hours):\n" + resetUrl
        );

        mailSender.send(mail);
    }
}
