package org.msnotificaciones.infrastructure.adapter.out.messaging;

import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.domain.port.out.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMessagingAdapter implements NotificacionPort {
    private static final Logger log = LoggerFactory.getLogger(NotificacionMessagingAdapter.class);

    @Override
    public void enviarNotificacion(CupoAsignadoEvent evento) {
        // El adaptador decide qué canales usar basándose en los datos disponibles
        log.info("===[ INICIO DE PROCESO DE NOTIFICACIÓN ]===");

        if (evento.email() != null) {
            this.enviarEmailSimulado(evento);
        }

        if (evento.telefono() != null) {
            this.enviarSMSSimulado(evento);
        }

        log.info("===[ FIN DE PROCESO DE NOTIFICACIÓN ]===");
    }

    private void enviarEmailSimulado(CupoAsignadoEvent evento) {
        log.info("[EMAIL] Enviando correo a: {} | Asunto: Nuevo cupo en {}",
                evento.email(), evento.especialidad());
    }

    private void enviarSMSSimulado(CupoAsignadoEvent evento) {
        log.info("[SMS] Enviando mensaje al móvil: {} | RUT: {}",
                evento.telefono(), evento.pacienteRut());
    }
}