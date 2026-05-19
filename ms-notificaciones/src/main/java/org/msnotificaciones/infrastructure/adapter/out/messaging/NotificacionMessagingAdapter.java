package org.msnotificaciones.infrastructure.adapter.out.messaging;

import org.msnotificaciones.domain.port.out.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMessagingAdapter implements NotificacionPort {
    private static final Logger log = LoggerFactory.getLogger(NotificacionMessagingAdapter.class);

    private final JavaMailSender mailSender;
    private final boolean emailEnabled;
    private final String emailFrom;
    private final String emailSubject;

    public NotificacionMessagingAdapter(
            JavaMailSender mailSender,
            @Value("${notificaciones.email.enabled}") boolean emailEnabled,
            @Value("${notificaciones.email.from}") String emailFrom,
            @Value("${notificaciones.email.subject}") String emailSubject
    ) {
        this.mailSender = mailSender;
        this.emailEnabled = emailEnabled;
        this.emailFrom = emailFrom;
        this.emailSubject = emailSubject;
    }

    @Override
    public void enviarEmail(String email, String mensaje) {
        if (!emailEnabled) {
            log.info("[EMAIL SIMULADO] Para: {} | Texto: {}", email, mensaje);
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(email);
        mailMessage.setSubject(emailSubject);
        mailMessage.setText(mensaje);

        mailSender.send(mailMessage);
        log.info("[EMAIL ENVIADO] Para: {}", email);
    }

    @Override
    public void enviarSMS(String telefono, String mensaje) {
        log.info("[SALIDA TECNICA - SMS] Enviando a: {} | Texto: {}", telefono, mensaje);
    }
}
