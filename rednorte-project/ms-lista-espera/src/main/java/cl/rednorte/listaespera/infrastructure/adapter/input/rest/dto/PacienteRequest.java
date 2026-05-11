package cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PacienteRequest(
        @NotBlank(message = "RUT es requerido")
        @Pattern(regexp = "^\\d{1,2}\\.?\\d{3}\\.?\\d{3}[-][0-9kK]$|^\\d{7,8}[-][0-9kK]$",
                message = "RUT debe estar en formato válido (ej: 12345678-9)")
        String rut,

        @NotBlank(message = "Nombre es requerido")
        String nombre,

        @NotBlank(message = "Apellido es requerido")
        String apellido,

        @Pattern(regexp = "^\\+?56\\d{9}$|^$", message = "Teléfono debe ser válido (ej: +56912345678)")
        String telefono,

        @Email(message = "Email debe ser válido")
        String email
) {}
