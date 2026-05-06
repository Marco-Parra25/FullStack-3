package org.msreasignacion.application.usecase;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // IMPORTANTE
import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.model.EstadoReasignacion;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.msreasignacion.domain.port.out.PacientePort;
import org.msreasignacion.domain.port.out.ReasignacionRepositoryPort;
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
    private final ReasignacionRepositoryPort repository;
    private final PacientePort pacientePort;
    private final EventoReasignacionPort eventoPort;

    public ReasignarCupoUseCase(ReasignacionRepositoryPort repository, PacientePort pacientePort, EventoReasignacionPort eventoPort) {
        this.repository = repository;
        this.pacientePort = pacientePort;
        this.eventoPort = eventoPort;
    }

    @Transactional
    // Agregamos el Circuit Breaker con el nombre definido en el application.yml
    @CircuitBreaker(name = "backendReasignacion", fallbackMethod = "fallbackReasignar")
    public void ejecutar(CupoLiberadoEvent evento) {
        log.info("===[ CAPA APLICACIÓN ]=== Iniciando reasignación para especialidad: {}", evento.especialidad());

        // 1. Buscar paciente prioritario
        Optional<Paciente> pacienteOpt = pacientePort.obtenerSiguientePaciente(evento.especialidad());

        if (pacienteOpt.isEmpty()) {
            log.warn("XXX Sin pacientes en espera para {}", evento.especialidad());
            return;
        }

        Paciente paciente = pacienteOpt.get();

        // 2. Crear registro inicial
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

            log.info("✅ Éxito: Paciente {} reasignado correctamente.", paciente.getRut());

        } catch (Exception e) {
            log.error("❌ Fallo crítico en reasignación: {}", e.getMessage());
            reasignacion.setEstado(EstadoReasignacion.FALLIDA.name());
            repository.guardar(reasignacion);
            // Re-lanzamos la excepción para que el Circuit Breaker la cuente como fallo
            throw e;
        }
    }

    /**
     * MÉTODO FALLBACK: Se ejecuta cuando el circuito está abierto o hay errores persistentes.
     */
    public void fallbackReasignar(CupoLiberadoEvent evento, Throwable t) {
        log.error("🛑 [CIRCUIT BREAKER] El proceso de reasignación está degradado o fuera de servicio.");
        log.error("Motivo: {}. El cupo ID {} quedará pendiente para reintento manual.", t.getMessage(), evento.cupoId());
        // Aquí podrías guardar en una tabla de "pendientes_manual" o simplemente alertar.
    }

    public Optional<Reasignacion> obtenerDetalle(UUID id) {
        return repository.buscarPorId(id);
    }
}