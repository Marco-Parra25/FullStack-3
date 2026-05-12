package org.msreasignacion.domain.event;

public record CupoLiberadoEvent(
        String cupoId,
        String especialidad
) {}