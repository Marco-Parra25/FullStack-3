package org.msreasignacion.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReasignacionDTO(
        UUID id,
        String pacienteRut,
        String especialidad,
        String estado,
        LocalDateTime fechaAsignacion
) {}