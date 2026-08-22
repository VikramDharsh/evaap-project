package com.evaap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    /**
     * Sends the 6-digit OTP to the given address for email verification.
     * Called from AuthService.register() and AuthService.resendOtp().
     */
    public void sendOtpEmail(String toAddress, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject("EVAAP — Verify your email");
        message.setText(
                "Your EVAAP verification code is: " + otp + "\n\n" +
                        "This code expires in 10 minutes. If you didn't request this, you can ignore this email."
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log the real SMTP cause here — GlobalExceptionHandler only logs
            // this exception's message, not its cause, so without this the
            // actual reason (auth failure, timeout, etc.) is invisible.
            log.error("Failed to send OTP email to {}", toAddress, e);
            throw new EmailSendException("Failed to send verification email", e);
        }
    }

    /**
     * Thrown when the OTP email fails to send. Add a handler for this in
     * GlobalExceptionHandler so it returns a clean JSON error instead of a
     * raw 500 with a stack trace.
     */
    public static class EmailSendException extends RuntimeException {
        public EmailSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}