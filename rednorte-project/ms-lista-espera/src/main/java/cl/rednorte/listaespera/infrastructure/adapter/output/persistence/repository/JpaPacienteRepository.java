package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository;

import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPacienteRepository extends JpaRepository<PacienteEntity, Long> {
}
