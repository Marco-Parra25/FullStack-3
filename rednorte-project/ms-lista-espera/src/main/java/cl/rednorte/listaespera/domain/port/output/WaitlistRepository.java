package cl.rednorte.listaespera.domain.port.output;

import cl.rednorte.listaespera.domain.model.WaitlistItem;

import java.util.List;
import java.util.Optional;

public interface WaitlistRepository {

    WaitlistItem save(WaitlistItem item);

    Optional<WaitlistItem> findById(Long id);

    List<WaitlistItem> findAll();

    List<WaitlistItem> findByEspecialidad(String especialidad);

    List<WaitlistItem> findAllOrderByPrioridad();

    long countEnEspera();
}
