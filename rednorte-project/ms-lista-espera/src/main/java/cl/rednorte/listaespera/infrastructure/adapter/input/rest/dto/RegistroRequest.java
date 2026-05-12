package cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto;

import cl.rednorte.listaespera.domain.model.TipoAtencion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroRequest(
        @NotNull Long pacienteId,
        @NotNull TipoAtencion tipoAtencion,
        @NotBlank String especialidad
) {}
