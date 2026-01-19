package com.ecommerce.winz.service.implementation;

import com.ecommerce.winz.service.email.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendWelcomeEmail(String to, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("email", to);

            String html = templateEngine.process("welcome-email", context);
            sendHtmlMail(to, "Welcome to Winz Ecommerce", html);
        } catch (Exception e) {
            System.err.println("Welcome email failed: " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendResetPasswordEmail(String to, String name, String password) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("password", password);

            String html = templateEngine.process("reset-password", context);
            sendHtmlMail(to, "Password Reset", html);
        } catch (Exception e) {
            System.err.println("Reset password email failed: " + e.getMessage());
        }
    }

    private void sendHtmlMail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            // VERY IMPORTANT → Never throw
            System.err.println("Email sending failed: " + e.getMessage());
        }
    }
}
