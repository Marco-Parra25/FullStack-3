package cl.rednorte.listaespera.domain.port.output;

import cl.rednorte.listaespera.domain.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository {

    Optional<Paciente> findById(Long id);

    boolean existsByRut(String rut);

    Paciente save(Paciente paciente);

    List<Paciente> findAll();
}
