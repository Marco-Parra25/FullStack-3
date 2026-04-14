package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.mapper;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.PacienteEntity;

public class PacienteMapper {

    private PacienteMapper() {}

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
