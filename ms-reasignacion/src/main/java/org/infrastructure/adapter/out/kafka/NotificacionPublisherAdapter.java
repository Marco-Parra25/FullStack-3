package org.infrastructure.adapter.out.kafka;

import org.domain.event.CupoAsignadoEvent;
import org.domain.port.out.EventoReasignacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificacionPublisherAdapter implements EventoReasignacionPort {

    private static final Logger log = LoggerFactory.getLogger(NotificacionPublisherAdapter.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "cupo-asignado";

    public NotificacionPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publicarCupoAsignado(String pacienteRut, String telefono, String email, String especialidad) {
        CupoAsignadoEvent evento = new CupoAsignadoEvent(pacienteRut, telefono, email, especialidad);
        log.info("Publicando en Kafka (Topic: {}) para paciente RUT: {}", TOPIC, pacienteRut);
        kafkaTemplate.send(TOPIC, evento);
    }
}