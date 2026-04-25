package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.WaitlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaWaitlistRepository extends JpaRepository<WaitlistItemEntity, Long> {

    List<WaitlistItemEntity> findByEspecialidadAndEstadoOrderByPrioridadAscFechaIngresoAsc(
            String especialidad, EstadoEspera estado);

    List<WaitlistItemEntity> findByEstadoOrderByPrioridadAscFechaIngresoAsc(EstadoEspera estado);

    long countByEstado(EstadoEspera estado);
}
