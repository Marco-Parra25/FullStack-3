package org.msnotificaciones.application.usecase;

import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.domain.port.out.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificarPacienteUseCase {
    private static final Logger log = LoggerFactory.getLogger(NotificarPacienteUseCase.class);
    private final NotificacionPort notificacionPort;

    public NotificarPacienteUseCase(NotificacionPort notificacionPort) {
        this.notificacionPort = notificacionPort;
    }

    /**
     * Ejecuta la lógica de negocio de la notificación.
     * Recibe el objeto de dominio completo para mantener la flexibilidad.
     */
    public void ejecutar(CupoAsignadoEvent evento) {
        log.info("===[ CAPA APLICACIÓN ]=== Procesando lógica de notificación para RUT: {}", evento.pacienteRut());

        // 1. Validaciones de Negocio (Ejemplo)
        if (evento.pacienteRut() == null || evento.pacienteRut().isEmpty()) {
            log.error("No se puede procesar: El RUT del paciente es obligatorio.");
            return;
        }

        // 2. Orquestación: Delegamos al puerto la responsabilidad técnica de notificar.
        // El caso de uso no necesita saber CÓMO se envía el mensaje (Email o SMS),
        // solo le dice al puerto que lo haga usando los datos del evento.
        notificacionPort.enviarNotificacion(evento);

        log.info("===[ CAPA APLICACIÓN ]=== Lógica completada para RUT: {}", evento.pacienteRut());
    }
}