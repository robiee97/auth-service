package com.etsyclone.auth_service.service;

import com.etsyclone.auth_service.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(User user) throws MessagingException {
        String verificationUrl = baseUrl + "/api/auth/verify?token=" + user.getVerificationToken();

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("verificationUrl", verificationUrl);

        String emailContent = templateEngine.process("verification-email", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(user.getEmail());
        helper.setSubject("Verify your email address");
        helper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }
}