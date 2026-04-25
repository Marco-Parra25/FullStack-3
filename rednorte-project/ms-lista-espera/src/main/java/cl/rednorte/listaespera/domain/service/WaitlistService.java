package cl.rednorte.listaespera.domain.service;

import cl.rednorte.listaespera.domain.factory.WaitlistItemFactory;
import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.model.TipoAtencion;
import cl.rednorte.listaespera.domain.model.WaitlistItem;
import cl.rednorte.listaespera.domain.port.input.WaitlistUseCase;
import cl.rednorte.listaespera.domain.port.output.PacienteRepository;
import cl.rednorte.listaespera.domain.port.output.WaitlistRepository;
import cl.rednorte.listaespera.infrastructure.exception.PacienteNotFoundException;
import cl.rednorte.listaespera.infrastructure.exception.WaitlistItemNotFoundException;

import java.util.List;

public class WaitlistService implements WaitlistUseCase {

    private final WaitlistRepository waitlistRepository;
    private final PacienteRepository pacienteRepository;

    public WaitlistService(WaitlistRepository waitlistRepository, PacienteRepository pacienteRepository) {
        this.waitlistRepository = waitlistRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public WaitlistItem registrarPaciente(Long pacienteId, TipoAtencion tipo, String especialidad) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new PacienteNotFoundException(pacienteId));

        WaitlistItem item = WaitlistItemFactory.create(tipo, paciente, especialidad);
        return waitlistRepository.save(item);
    }

    @Override
    public List<WaitlistItem> listarPorEspecialidad(String especialidad) {
        return waitlistRepository.findByEspecialidad(especialidad);
    }

    @Override
    public List<WaitlistItem> listarTodos() {
        return waitlistRepository.findAll();
    }

    @Override
    public WaitlistItem obtenerPorId(Long id) {
        return waitlistRepository.findById(id)
                .orElseThrow(() -> new WaitlistItemNotFoundException(id));
    }

    @Override
    public void cancelar(Long id) {
        WaitlistItem item = obtenerPorId(id);
        item.cancelar();
        waitlistRepository.save(item);
    }

    @Override
    public long contarEnEspera() {
        return waitlistRepository.countEnEspera();
    }

    @Override
    public List<WaitlistItem> listarPorPrioridad() {
        return waitlistRepository.findAllOrderByPrioridad();
    }
}
