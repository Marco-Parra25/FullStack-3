package cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto;

import cl.rednorte.listaespera.domain.model.WaitlistItem;

import java.time.LocalDate;

public record WaitlistResponse(
        Long id,
        String pacienteNombre,
        String pacienteRut,
        String tipoAtencion,
        String especialidad,
        int prioridad,
        String estado,
        LocalDate fechaIngreso
) {
    public static WaitlistResponse from(WaitlistItem item) {
        return new WaitlistResponse(
                item.getId(),
                item.getPaciente().getNombre() + " " + item.getPaciente().getApellido(),
                item.getPaciente().getRut(),
                item.getTipoAtencion().name(),
                item.getEspecialidad(),
                item.getPrioridad(),
                item.getEstado().name(),
                item.getFechaIngreso()
        );
    }
}
