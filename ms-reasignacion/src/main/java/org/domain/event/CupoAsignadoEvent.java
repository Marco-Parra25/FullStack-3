package org.domain.event;

public record CupoAsignadoEvent(
        String pacienteRut,
        String telefono,
        String email,
        String especialidad
) {}