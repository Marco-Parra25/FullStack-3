package org.msnotificaciones.infrastructure.adapter.out.messaging;

import org.msnotificaciones.domain.port.out.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMessagingAdapter implements NotificacionPort {
    private static final Logger log = LoggerFactory.getLogger(NotificacionMessagingAdapter.class);

    @Override
    public void enviarEmail(String email, String mensaje) {
        // Aquí no hay "IFs", solo la acción técnica
        log.info("📧 [SALIDA TÉCNICA - EMAIL] Enviando a: {} | Texto: {}", email, mensaje);
    }

    @Override
    public void enviarSMS(String telefono, String mensaje) {
        // Aquí no hay "IFs", solo la acción técnica
        log.info("📱 [SALIDA TÉCNICA - SMS] Enviando a: {} | Texto: {}", telefono, mensaje);
    }
}