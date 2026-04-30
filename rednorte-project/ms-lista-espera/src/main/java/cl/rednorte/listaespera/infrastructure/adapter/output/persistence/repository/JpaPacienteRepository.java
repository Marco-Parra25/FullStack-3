package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository;

import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.PacienteDto;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaPacienteRepository extends JpaRepository<PacienteEntity, Long> {

    boolean existsByRut(String rut);

    // ── Consultas que devuelven DTO (no exponen la entidad al dominio) ──────────

    @Query("""
            SELECT new cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.PacienteDto(
                p.id, p.rut, p.nombre, p.apellido, p.telefono, p.email
            )
            FROM PacienteEntity p
            WHERE p.id = :id
            """)
    Optional<PacienteDto> findDtoById(@Param("id") Long id);

    @Query("""
            SELECT new cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.PacienteDto(
                p.id, p.rut, p.nombre, p.apellido, p.telefono, p.email
            )
            FROM PacienteEntity p
            ORDER BY p.apellido ASC, p.nombre ASC
            """)
    List<PacienteDto> findAllDto();
}
