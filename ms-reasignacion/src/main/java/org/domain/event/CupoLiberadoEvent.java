package org.domain.event;

public record CupoLiberadoEvent(
        String cupoId,
        String especialidad
) {}