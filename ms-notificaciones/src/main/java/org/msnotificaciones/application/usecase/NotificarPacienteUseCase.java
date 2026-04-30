package org.msnotificaciones.application.usecase;

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

    public void ejecutar(String rut, String email, String telefono, String especialidad) {
        String mensajeBody = String.format(
                "Estimado paciente con RUT %s: Se ha reasignado su cupo para la especialidad de %s.",
                rut, especialidad
        );

        log.info("Procesando notificación en Dominio para RUT: {} y Especialidad: {}", rut, especialidad);

        // Enviamos el cuerpo del mensaje y los datos meta para trazabilidad
        notificacionPort.enviarEmail(email, mensajeBody, rut, especialidad);
        notificacionPort.enviarSMS(telefono, mensajeBody, rut, especialidad);
    }
}