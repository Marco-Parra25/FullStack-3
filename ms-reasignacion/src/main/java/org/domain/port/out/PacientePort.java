package org.domain.port.out;

import org.domain.model.Paciente;
import java.util.Optional;

public interface PacientePort {
    Optional<Paciente> obtenerSiguientePaciente(String especialidad);
}