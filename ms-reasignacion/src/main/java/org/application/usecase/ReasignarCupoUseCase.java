package org.application.usecase;

import org.domain.event.CupoLiberadoEvent;
import org.domain.model.Paciente;
import org.domain.model.Reasignacion;
import org.domain.port.out.EventoReasignacionPort;
import org.domain.port.out.PacientePort;
import org.infrastructure.repository.ReasignacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReasignarCupoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReasignarCupoUseCase.class);
    private final ReasignacionRepository repository;
    private final PacientePort pacientePort;
    private final EventoReasignacionPort eventoPort;

    public ReasignarCupoUseCase(ReasignacionRepository repository, PacientePort pacientePort, EventoReasignacionPort eventoPort) {
        this.repository = repository;
        this.pacientePort = pacientePort;
        this.eventoPort = eventoPort;
    }

    public void ejecutar(CupoLiberadoEvent evento) {
        log.info("Buscando paciente en lista de espera para la especialidad: {}", evento.especialidad());

        Optional<Paciente> pacienteOpt = pacientePort.obtenerSiguientePaciente(evento.especialidad());

        if (pacienteOpt.isEmpty()) {
            log.warn("No hay pacientes en lista de espera para {}", evento.especialidad());
            return;
        }

        Paciente paciente = pacienteOpt.get();

        Reasignacion nuevaReasignacion = new Reasignacion(
                UUID.randomUUID(),
                paciente.getRut(),
                evento.especialidad(),
                LocalDateTime.now(),
                "ASIGNADO",
                UUID.fromString(evento.cupoId())
        );

        repository.save(nuevaReasignacion);
        log.info("Cupo reasignado exitosamente al paciente con RUT {}", paciente.getRut());

        eventoPort.publicarCupoAsignado(
                paciente.getRut(),
                paciente.getTelefono(),
                paciente.getEmail(),
                evento.especialidad()
        );
    }
}