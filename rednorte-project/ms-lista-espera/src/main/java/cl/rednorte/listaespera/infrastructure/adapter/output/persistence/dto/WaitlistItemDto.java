package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.domain.model.TipoAtencion;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de persistencia para WaitlistItem.
 * Proyecta un join entre waitlist_items y pacientes en una sola consulta JPQL,
 * evitando exponer la entidad JPA directamente al dominio.
 */
public record WaitlistItemDto(
        Long id,
        Long pacienteId,
        String pacienteRut,
        String pacienteNombre,
        String pacienteApellido,
        String pacienteTelefono,
        String pacienteEmail,
        TipoAtencion tipoAtencion,
        String especialidad,
        int prioridad,
        EstadoEspera estado,
        LocalDate fechaIngreso,
        LocalDateTime fechaAsignacion
) {}
