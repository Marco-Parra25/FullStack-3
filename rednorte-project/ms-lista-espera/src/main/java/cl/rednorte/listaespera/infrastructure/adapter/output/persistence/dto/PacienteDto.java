package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto;

/**
 * DTO de persistencia para Paciente.
 * Usado por el repositorio JPA para proyectar resultados de consultas
 * sin exponer directamente la entidad al dominio.
 */
public record PacienteDto(
        Long id,
        String rut,
        String nombre,
        String apellido,
        String telefono,
        String email
) {}
