package org.msreasignacion.infrastructure.adapter.out.kafka;

import org.msreasignacion.domain.event.CupoAsignadoEvent;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NotificacionPublisherAdapter implements EventoReasignacionPort {

    private static final Logger log = LoggerFactory.getLogger(NotificacionPublisherAdapter.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final long sendTimeoutMs;

    public NotificacionPublisherAdapter(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${rednorte.kafka.topics.cupo-asignado}") String topic,
            @Value("${rednorte.kafka.send-timeout-ms}") long sendTimeoutMs
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.sendTimeoutMs = sendTimeoutMs;
    }

    @Override
    public void publicarCupoAsignado(String pacienteRut, String telefono, String email, String especialidad) {
        CupoAsignadoEvent evento = new CupoAsignadoEvent(pacienteRut, telefono, email, especialidad);
        log.info("Publicando en Kafka (Topic: {}) para paciente RUT: {}", topic, pacienteRut);

        try {
            kafkaTemplate.send(topic, pacienteRut, evento).get(sendTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo publicar evento cupo-asignado", e);
        }
    }
}
