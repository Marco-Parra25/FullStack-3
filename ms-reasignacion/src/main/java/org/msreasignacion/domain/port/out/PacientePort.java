package org.msreasignacion.domain.port.out;

import org.msreasignacion.domain.model.Paciente;
import java.util.Optional;

public interface PacientePort {
    Optional<Paciente> obtenerSiguientePaciente(String especialidad);
}