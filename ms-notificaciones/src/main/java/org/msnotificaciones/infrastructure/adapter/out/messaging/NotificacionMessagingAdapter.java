package org.msnotificaciones.infrastructure.adapter.out.messaging;

import org.msnotificaciones.domain.port.out.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMessagingAdapter implements NotificacionPort {
    private static final Logger log = LoggerFactory.getLogger(NotificacionMessagingAdapter.class);

    @Override
    public void enviarEmail(String destinatario, String mensaje, String rut, String especialidad) {
        // Log profesional simulando envío real
        log.info("[EMAIL ADAPTER] -> Destinatario: {} | Paciente: {} | Especialidad: {}",
                destinatario, rut, especialidad);
        log.debug("Cuerpo del Email enviado: {}", mensaje);
    }

    @Override
    public void enviarSMS(String telefono, String mensaje, String rut, String especialidad) {
        // Log profesional simulando envío real
        log.info("[SMS ADAPTER] -> Teléfono: {} | Paciente: {} | Especialidad: {}",
                telefono, rut, especialidad);
        log.debug("Cuerpo del SMS enviado: {}", mensaje);
    }
}