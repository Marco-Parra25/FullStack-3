package org.msreasignacion.application.usecase;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.msreasignacion.domain.port.out.PacientePort;
import org.msreasignacion.domain.port.out.ReasignacionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReasignarCupoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReasignarCupoUseCase.class);

    private final ReasignacionRepositoryPort repository;
    private final PacientePort pacientePort;
    private final EventoReasignacionPort eventoPort;

    public ReasignarCupoUseCase(
            ReasignacionRepositoryPort repository,
            PacientePort pacientePort,
            EventoReasignacionPort eventoPort
    ) {
        this.repository = repository;
        this.pacientePort = pacientePort;
        this.eventoPort = eventoPort;
    }

    @Transactional
    @CircuitBreaker(name = "backendReasignacion", fallbackMethod = "fallbackReasignar")
    public void ejecutar(CupoLiberadoEvent evento) {
        log.info("Iniciando reasignacion para especialidad: {}", evento.especialidad());

        Optional<Paciente> pacienteOpt = pacientePort.obtenerSiguientePaciente(evento.especialidad());

        if (pacienteOpt.isEmpty()) {
            log.warn("Sin pacientes en espera para {}", evento.especialidad());
            return;
        }

        Paciente paciente = pacienteOpt.get();
        Reasignacion reasignacion = crearReasignacionPendiente(evento, paciente);
        guardar(reasignacion);

        try {
            marcarComoAsignada(reasignacion);
            notificarCupoAsignado(paciente, evento.especialidad());
            marcarComoCompletada(reasignacion);

            log.info("Paciente {} reasignado correctamente.", paciente.getRut());
        } catch (Exception e) {
            log.error("Fallo critico en reasignacion para cupo {}: {}", evento.cupoId(), e.getMessage(), e);
            marcarComoFallida(reasignacion);
        }
    }

    private Reasignacion crearReasignacionPendiente(CupoLiberadoEvent evento, Paciente paciente) {
        return Reasignacion.crearPendiente(
                paciente.getRut(),
                evento.especialidad(),
                normalizarCupoId(evento.cupoId())
        );
    }

    private UUID normalizarCupoId(String cupoId) {
        try {
            return UUID.fromString(cupoId);
        } catch (RuntimeException e) {
            return UUID.nameUUIDFromBytes(("cupo-liberado:" + cupoId).getBytes(StandardCharsets.UTF_8));
        }
    }

    private void marcarComoAsignada(Reasignacion reasignacion) {
        reasignacion.marcarAsignada();
        guardar(reasignacion);
    }

    private void notificarCupoAsignado(Paciente paciente, String especialidad) {
        eventoPort.publicarCupoAsignado(
                paciente.getRut(),
                paciente.getTelefono(),
                paciente.getEmail(),
                especialidad
        );
    }

    private void marcarComoCompletada(Reasignacion reasignacion) {
        reasignacion.marcarCompletada();
        guardar(reasignacion);
    }

    private void marcarComoFallida(Reasignacion reasignacion) {
        reasignacion.marcarFallida();
        guardar(reasignacion);
    }

    private void guardar(Reasignacion reasignacion) {
        repository.guardar(reasignacion);
    }

    public void fallbackReasignar(CupoLiberadoEvent evento, Throwable t) {
        log.error(
                "[CIRCUIT BREAKER] Proceso de reasignacion degradado. Cupo ID {} queda pendiente para revision manual. Motivo: {}",
                evento.cupoId(),
                t.getMessage(),
                t
        );
    }

    public Optional<Reasignacion> obtenerDetalle(UUID id) {
        return repository.buscarPorId(id);
    }
}
