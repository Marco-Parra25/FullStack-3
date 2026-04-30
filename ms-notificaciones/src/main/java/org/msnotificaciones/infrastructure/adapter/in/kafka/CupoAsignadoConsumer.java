package org.msnotificaciones.infrastructure.adapter.in.kafka;

import org.msnotificaciones.application.usecase.NotificarPacienteUseCase;
import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CupoAsignadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(CupoAsignadoConsumer.class);
    private final NotificarPacienteUseCase useCase;

    public CupoAsignadoConsumer(NotificarPacienteUseCase useCase) {
        this.useCase = useCase;
    }

    @KafkaListener(topics = "cupo-asignado", groupId = "notificaciones-group")
    public void consumir(CupoAsignadoEvent evento) {
        // EL TRY-CATCH VA AQUÍ PROTEGIENDO LA EJECUCIÓN DEL CASO DE USO
        try {
            log.info(">>> Evento recibido de Kafka para paciente: {}", evento.pacienteRut());

            // Llamada al caso de uso (donde está la lógica de negocio)
            useCase.ejecutar(
                    evento.pacienteRut(),
                    evento.email(),
                    evento.telefono(),
                    evento.especialidad()
            );

            log.info(">>> Notificación procesada exitosamente para RUT: {}", evento.pacienteRut());

        } catch (Exception e) {
            // Si algo falla (ej. el servicio de email está caído), capturamos el error aquí
            log.error("XXX Error al procesar la notificación para el paciente {}: {}",
                    evento.pacienteRut(), e.getMessage());

            // Aquí podrías implementar una lógica de reintento o enviar a una Dead Letter Queue (DLQ)
        }
    }
}