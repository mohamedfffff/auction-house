package com.example.lusterz.auction_house.infrastructure.notification;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class EmailService{

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${app.fromEmail}")
    private String fromEmail;

    public void sendWinnerEmail(String toEmail, String winnerName, String item, BigDecimal price) {

        try {
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

            Thread.sleep(2000);
            mailSender.send(message);

            log.info("Winner email sent to winner : {}", winnerName);

        } catch (InterruptedException e) {
            // prevent java from clearing the logs
            Thread.currentThread().interrupt();
        }
    }

    public void sendSellerEmail(String toEmail, String seller, String item, BigDecimal price, String winnerName) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your Item Has Been Sold!!!");
            message.setText(
                "Congratulations!\n\n" +
                "Your item " + item + " has been sold to " + winnerName + "\n" +
                "Final price " + price + "\n"
            );

            Thread.sleep(2000);
            mailSender.send(message);

            log.info("Email sent to seller : {}", seller);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void sendExpiredEmail(String toEmail, String sellerName, String item) {
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your Item Has Expired!!!");
            message.setText(
                "Unfortunetely\n\n" +
                "Your item " + item + " did not find a bidder\n"
            );

            Thread.sleep(2000);
            mailSender.send(message);

            log.info("Email sent to seller : {}", sellerName);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendVerificationEmail(String toEmail, String username, String token) {

        try {
            Context context = new Context();

            context.setVariable("name", username);
            context.setVariable("verifyUrl", "http://localhost:8080/api/v1/auth/verify/" + token);

            String htmlContent = templateEngine.process("verification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject("Complete your registration");
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);

            Thread.sleep(2000);
            mailSender.send(message);

            log.info("Verification email sent to user : {}", username);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (MessagingException e) {
            log.error("Failed to send verification email to user : {}. Reason {}", username, e.getMessage());
        }
    }
}
