package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.mapper;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.PacienteDto;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.PacienteEntity;

public class PacienteMapper {

    private PacienteMapper() {}

    /** Entity → Domain (usado al guardar y recuperar por entity) */
    public static Paciente toDomain(PacienteEntity entity) {
        return Paciente.builder()
                .id(entity.getId())
                .rut(entity.getRut())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .build();
    }

    /** DTO de persistencia → Domain (usado en consultas JPQL con proyección) */
    public static Paciente toDomain(PacienteDto dto) {
        return Paciente.builder()
                .id(dto.id())
                .rut(dto.rut())
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .telefono(dto.telefono())
                .email(dto.email())
                .build();
    }

    /** Domain → Entity (usado al persistir) */
    public static PacienteEntity toEntity(Paciente domain) {
        return PacienteEntity.builder()
                .id(domain.getId())
                .rut(domain.getRut())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .telefono(domain.getTelefono())
                .email(domain.getEmail())
                .build();
    }
}
