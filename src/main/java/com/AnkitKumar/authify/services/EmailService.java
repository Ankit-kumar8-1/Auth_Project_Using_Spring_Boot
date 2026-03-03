package com.AnkitKumar.authify.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Service
@RequiredArgsConstructor
public class EmailService {

    private  final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Value("${server.servlet.context-path}")
    private String baseUrl;

    public  void sendWelcomeEmail(String toEmail, String name ,String token){


        String verificationUrl =  "localhost:8080/api/v1.0/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify Your Email - Authify");
        message.setText(
                "Hello " + name +
                        "\n\nPlease click the link below to verify your account:\n\n"
                        + verificationUrl +
                        "\n\nThis link will expire in 30 minutes."
                        + "\n\nRegards,\nAuthify Team"
        );

        mailSender.send(message);
    }


    public  void sendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for resetting your password is " + otp + ". Use this OTP to proceed with resetting password ");
        mailSender.send(message);
    }


    public void sendOtpEmail(String toEmail,String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Account Verification OTP");
        message.setText("Your OTP is "+ otp +"Verify your account using this OTP");
        mailSender.send(message);
    }
}
