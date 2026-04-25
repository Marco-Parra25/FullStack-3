package cl.rednorte.listaespera.domain.port.input;

import cl.rednorte.listaespera.domain.model.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteUseCase {
    Paciente registrar(Paciente paciente);
    Optional<Paciente> obtenerPorId(Long id);
    List<Paciente> listarTodos();
}
