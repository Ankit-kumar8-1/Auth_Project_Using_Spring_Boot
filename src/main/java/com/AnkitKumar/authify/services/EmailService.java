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

    public  void sendWelcomeEmail(String toEmail, String name ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to our PlatForm");
        message.setText("Hello" + name +"\n\nThanks for registering with us!\n\nRegards\n Authify Team");

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
