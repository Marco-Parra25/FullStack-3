package cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoRequest(
        @NotNull EstadoEspera estado
) {}
