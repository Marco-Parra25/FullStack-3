package cl.rednorte.listaespera.domain.port.output;

import cl.rednorte.listaespera.domain.model.Paciente;

import java.util.Optional;

public interface PacienteRepository {

    Optional<Paciente> findById(Long id);

    Paciente save(Paciente paciente);
}
