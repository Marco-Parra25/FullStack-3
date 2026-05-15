package org.msnotificaciones.infrastructure.adapter.in.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Esto genera automáticamente los getPacienteRut, getEmail, etc.
@AllArgsConstructor
@NoArgsConstructor
public class CupoAsignadoDTO {
    private String pacienteRut;
    private String email;
    private String telefono;
    private String especialidad;
}