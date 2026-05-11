package cl.rednorte.listaespera.domain.port.input;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.domain.model.TipoAtencion;
import cl.rednorte.listaespera.domain.model.WaitlistItem;

import java.util.List;

public interface WaitlistUseCase {

    WaitlistItem registrarPaciente(Long pacienteId, TipoAtencion tipo, String especialidad);

    List<WaitlistItem> listarPorEspecialidad(String especialidad);

    List<WaitlistItem> listarTodos();

    WaitlistItem obtenerPorId(Long id);

    void cancelar(Long id);

    WaitlistItem actualizarEstado(Long id, EstadoEspera estado);

    long contarEnEspera();

    List<WaitlistItem> listarPorPrioridad();
}
