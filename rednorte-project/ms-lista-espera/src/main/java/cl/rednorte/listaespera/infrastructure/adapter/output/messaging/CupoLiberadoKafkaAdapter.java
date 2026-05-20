package cl.rednorte.listaespera.infrastructure.adapter.output.messaging;

import cl.rednorte.listaespera.domain.event.CupoLiberadoEvent;
import cl.rednorte.listaespera.domain.port.output.CupoLiberadoPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CupoLiberadoKafkaAdapter implements CupoLiberadoPublisherPort {

    private static final String TOPIC = "cupo-liberado";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public CupoLiberadoKafkaAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publicar(CupoLiberadoEvent event) {
        String mensaje = String.format(
            "{\"cupoId\":\"%s\",\"especialidad\":\"%s\"}",
            event.cupoId(),
            event.especialidad()
        );
        kafkaTemplate.send(TOPIC, event.cupoId(), mensaje);
    }
}
