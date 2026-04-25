package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.mapper;

import cl.rednorte.listaespera.domain.model.WaitlistItem;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.WaitlistItemEntity;

public class WaitlistItemMapper {

    private WaitlistItemMapper() {}

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
