package cl.rednorte.listaespera.domain.service;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.port.input.PacienteUseCase;
import cl.rednorte.listaespera.domain.port.output.PacienteRepository;

import java.util.List;
import java.util.Optional;

public class PacienteService implements PacienteUseCase {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Paciente registrar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public Optional<Paciente> obtenerPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }
}
