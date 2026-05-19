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

    public void ejecutar(CupoAsignadoEvent evento) {
        log.info("===[ CAPA APLICACIÓN ]=== Iniciando lógica centralizada");

        // 1. VALIDACIÓN DE NEGOCIO
        if (evento.pacienteRut() == null || evento.especialidad() == null) {
            log.warn("XXX Datos insuficientes: RUT o Especialidad ausentes.");
            return;
        }

        // 2. CONSTRUCCIÓN DEL MENSAJE (Regla de negocio: qué le decimos al paciente)
        String cuerpoMensaje = String.format(
                "Estimado paciente, se ha asignado su cupo para %s. RUT: %s.",
                evento.especialidad(), evento.pacienteRut()
        );

        if (evento.email() != null && !evento.email().isBlank()) {
            log.info("--- Decision: Usar canal EMAIL");
            try {
                notificacionPort.enviarEmail(evento.email(), cuerpoMensaje);
            } catch (Exception e) {
                log.error("XXX Error al enviar email a {}: {}", evento.email(), e.getMessage());
            }
        }

        if (evento.telefono() != null && !evento.telefono().isBlank()) {
            log.info("--- Decision: Usar canal SMS");
            try {
                notificacionPort.enviarSMS(evento.telefono(), cuerpoMensaje);
            } catch (Exception e) {
                log.error("XXX Error al enviar SMS a {}: {}", evento.telefono(), e.getMessage());
            }
        }

        log.info("===[ CAPA APLICACIÓN ]=== Proceso finalizado correctamente.");
    }
}
