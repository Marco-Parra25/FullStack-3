package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.mapper;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.model.WaitlistItem;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.WaitlistItemDto;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.WaitlistItemEntity;

public class WaitlistItemMapper {

    private WaitlistItemMapper() {}

    /** Entity → Domain (usado al guardar) */
    public static WaitlistItem toDomain(WaitlistItemEntity entity) {
        return WaitlistItem.builder()
                .id(entity.getId())
                .paciente(PacienteMapper.toDomain(entity.getPaciente()))
                .tipoAtencion(entity.getTipoAtencion())
                .especialidad(entity.getEspecialidad())
                .prioridad(entity.getPrioridad())
                .estado(entity.getEstado())
                .fechaIngreso(entity.getFechaIngreso())
                .fechaAsignacion(entity.getFechaAsignacion())
                .build();
    }

    /** DTO de persistencia → Domain (usado en consultas JPQL con proyección) */
    public static WaitlistItem toDomain(WaitlistItemDto dto) {
        Paciente paciente = Paciente.builder()
                .id(dto.pacienteId())
                .rut(dto.pacienteRut())
                .nombre(dto.pacienteNombre())
                .apellido(dto.pacienteApellido())
                .telefono(dto.pacienteTelefono())
                .email(dto.pacienteEmail())
                .build();

        return WaitlistItem.builder()
                .id(dto.id())
                .paciente(paciente)
                .tipoAtencion(dto.tipoAtencion())
                .especialidad(dto.especialidad())
                .prioridad(dto.prioridad())
                .estado(dto.estado())
                .fechaIngreso(dto.fechaIngreso())
                .fechaAsignacion(dto.fechaAsignacion())
                .build();
    }

    /** Actualiza una entidad existente con los datos del domain (usado al cancelar/modificar) */
    public static WaitlistItemEntity toEntity(WaitlistItemEntity existing, WaitlistItem domain) {
        existing.setTipoAtencion(domain.getTipoAtencion());
        existing.setEspecialidad(domain.getEspecialidad());
        existing.setPrioridad(domain.getPrioridad());
        existing.setEstado(domain.getEstado());
        existing.setFechaIngreso(domain.getFechaIngreso());
        existing.setFechaAsignacion(domain.getFechaAsignacion());
        return existing;
    }
}
