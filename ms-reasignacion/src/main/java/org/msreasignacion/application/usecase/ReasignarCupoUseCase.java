package org.msreasignacion.application.usecase;

import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.model.EstadoReasignacion;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.msreasignacion.domain.port.out.PacientePort;
import org.msreasignacion.domain.port.out.ReasignacionRepositoryPort; // Cambiado a Puerto
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReasignarCupoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReasignarCupoUseCase.class);
    private final ReasignacionRepositoryPort repository; // Usamos el puerto
    private final PacientePort pacientePort;
    private final EventoReasignacionPort eventoPort;

    public ReasignarCupoUseCase(ReasignacionRepositoryPort repository, PacientePort pacientePort, EventoReasignacionPort eventoPort) {
        this.repository = repository;
        this.pacientePort = pacientePort;
        this.eventoPort = eventoPort;
    }

    @Transactional
    public void ejecutar(CupoLiberadoEvent evento) {
        log.info("Iniciando reasignación para especialidad: {}", evento.especialidad());

        // 1. Buscar paciente prioritario
        Optional<Paciente> pacienteOpt = pacientePort.obtenerSiguientePaciente(evento.especialidad());

        if (pacienteOpt.isEmpty()) {
            log.warn("Sin pacientes en espera para {}", evento.especialidad());
            return;
        }

        Paciente paciente = pacienteOpt.get();

        // 2. Crear registro inicial (PENDIENTE)
        Reasignacion reasignacion = new Reasignacion(
                UUID.randomUUID(),
                paciente.getRut(),
                evento.especialidad(),
                LocalDateTime.now(),
                EstadoReasignacion.PENDIENTE.name(),
                UUID.fromString(evento.cupoId())
        );
        repository.guardar(reasignacion);

        try {
            // 3. Cambiar a ASIGNADO y notificar
            reasignacion.setEstado(EstadoReasignacion.ASIGNADO.name());

            eventoPort.publicarCupoAsignado(
                    paciente.getRut(),
                    paciente.getTelefono(),
                    paciente.getEmail(),
                    evento.especialidad()
            );

            // 4. Marcar como COMPLETADO
            reasignacion.setEstado(EstadoReasignacion.COMPLETADO.name());
            repository.guardar(reasignacion);

            log.info("Éxito: Paciente {} reasignado.", paciente.getRut());

        } catch (Exception e) {
            log.error("Fallo en reasignación: {}", e.getMessage());
            reasignacion.setEstado(EstadoReasignacion.FALLIDA.name());
            repository.guardar(reasignacion);
        }
    }

    // Método adicional para el Controller
    public Optional<Reasignacion> obtenerDetalle(UUID id) {
        return repository.buscarPorId(id);
    }
}