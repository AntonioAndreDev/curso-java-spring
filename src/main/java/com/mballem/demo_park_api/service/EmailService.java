package com.mballem.demo_park_api.service;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    private final String RESEND_KEY = System.getenv("resend_api_key");

    public void sendEmail(String to, String subject, String htmlContent) {
        Resend resend = new Resend(RESEND_KEY);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <onboarding@resend.dev>")
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            log.info("Erro ao enviar email: {}", e.getMessage());
        }
    }
}