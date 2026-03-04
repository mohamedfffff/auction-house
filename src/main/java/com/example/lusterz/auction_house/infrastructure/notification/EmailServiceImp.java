package com.example.lusterz.auction_house.infrastructure.notification;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class EmailServiceImp implements EmailService{

    private final JavaMailSender mailSender;
    @Value("${app.fromEmail}")
    private String fromEmail;

    @Override
    public void sendWinnerEmail(String toEmail, String item, BigDecimal price) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("You Won The Auction!!!");
        message.setText(
            "Congratulations!\n\n" +
            "You had the winning bid for " + item + "\n" +
            "Final price " + price + "\n" +
            "Access your account to complete the payment"
        );

        mailSender.send(message);
    }

    @Override
    public void sendSellerEmail(String toEmail, String item, String winner, BigDecimal price) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Item Has Been Sold!!!");
        message.setText(
            "Congratulations!\n\n" +
            "Your item " + item + " has been sold to " + winner + "\n" +
            "Final price " + price + "\n"
        );

        mailSender.send(message);
    }
    

    @Override
    public void sendExpiredEmail(String toEmail, String item) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Item Has Expired!!!");
        message.setText(
            "Unfortunetely\n\n" +
            "Your item " + item + " did not find a bidder\n"
        );

        mailSender.send(message);
    }
}
