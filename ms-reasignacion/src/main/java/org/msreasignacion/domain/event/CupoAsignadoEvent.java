package org.msreasignacion.domain.event;

public record CupoAsignadoEvent(
        String pacienteRut,
        String telefono,
        String email,
        String especialidad
) {}