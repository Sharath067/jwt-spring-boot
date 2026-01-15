package com.ecommerce.winz.service.email;

public interface EmailService {
    void sendWelcomeEmail(String to, String name);
    void sendResetPasswordEmail(String to, String name, String password);
}
