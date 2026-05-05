package org.msnotificaciones.infrastructure.adapter.in.kafka;

import org.msnotificaciones.application.usecase.NotificarPacienteUseCase;
import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.infrastructure.adapter.in.kafka.dto.CupoAsignadoDTO;
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
    public void consumir(CupoAsignadoDTO dto) { // 1. Recibe el DTO técnico de Kafka
        try {
            log.info("===[ ADAPTADOR ENTRADA ]=== Mensaje recibido desde Kafka para: {}", dto.getPacienteRut());

            // 2. MAPEO: Convertimos el DTO (Infra) al Objeto de Dominio (Event)
            // Esto protege tu lógica de negocio de cambios en el JSON de Kafka
            CupoAsignadoEvent eventoDeDominio = new CupoAsignadoEvent(
                    dto.getPacienteRut(),
                    dto.getEmail(),
                    dto.getTelefono(),
                    dto.getEspecialidad()
            );

            // 3. Ejecutamos el caso de uso pasando el objeto de dominio
            useCase.ejecutar(eventoDeDominio);

            log.info(">>> Flujo de notificación completado para RUT: {}", dto.getPacienteRut());

        } catch (Exception e) {
            log.error("XXX Error crítico en el Adaptador de Entrada: {}", e.getMessage());
        }
    }
}