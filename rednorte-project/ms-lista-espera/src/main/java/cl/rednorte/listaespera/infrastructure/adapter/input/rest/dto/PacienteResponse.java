package cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto;

import cl.rednorte.listaespera.domain.model.Paciente;

public record PacienteResponse(
        Long id,
        String rut,
        String nombre,
        String apellido,
        String telefono,
        String email
) {
    public static PacienteResponse from(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getRut(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getTelefono(),
                paciente.getEmail()
        );
    }
}
