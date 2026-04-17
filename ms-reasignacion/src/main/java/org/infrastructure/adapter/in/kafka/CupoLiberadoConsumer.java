package org.infrastructure.adapter.in.kafka;

import org.application.usecase.ReasignarCupoUseCase;
import org.domain.event.CupoLiberadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CupoLiberadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(CupoLiberadoConsumer.class);
    private final ReasignarCupoUseCase reasignarCupoUseCase;

    public CupoLiberadoConsumer(ReasignarCupoUseCase reasignarCupoUseCase) {
        this.reasignarCupoUseCase = reasignarCupoUseCase;
    }

    @KafkaListener(topics = "cupo-liberado", groupId = "reasignacion-grupo")
    public void consumirCupoLiberado(CupoLiberadoEvent evento) {
        log.info(">>> Evento recibido - Cupo Liberado: ID {}, Especialidad {}", evento.cupoId(), evento.especialidad());
        reasignarCupoUseCase.ejecutar(evento);
    }
}