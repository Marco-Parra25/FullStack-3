package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.WaitlistItemDto;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.WaitlistItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaWaitlistRepository extends JpaRepository<WaitlistItemEntity, Long> {

    long countByEstado(EstadoEspera estado);

    // ── Consultas que devuelven DTO (join explícito con paciente) ──────────────

    @Query("""
            SELECT new cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.WaitlistItemDto(
                w.id,
                w.paciente.id, w.paciente.rut, w.paciente.nombre, w.paciente.apellido,
                w.paciente.telefono, w.paciente.email,
                w.tipoAtencion, w.especialidad, w.prioridad, w.estado,
                w.fechaIngreso, w.fechaAsignacion
            )
            FROM WaitlistItemEntity w
            WHERE w.id = :id
            """)
    Optional<WaitlistItemDto> findDtoById(@Param("id") Long id);

    @Query("""
            SELECT new cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.WaitlistItemDto(
                w.id,
                w.paciente.id, w.paciente.rut, w.paciente.nombre, w.paciente.apellido,
                w.paciente.telefono, w.paciente.email,
                w.tipoAtencion, w.especialidad, w.prioridad, w.estado,
                w.fechaIngreso, w.fechaAsignacion
            )
            FROM WaitlistItemEntity w
            ORDER BY w.prioridad ASC, w.fechaIngreso ASC
            """)
    List<WaitlistItemDto> findAllDto();

    @Query("""
            SELECT new cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.WaitlistItemDto(
                w.id,
                w.paciente.id, w.paciente.rut, w.paciente.nombre, w.paciente.apellido,
                w.paciente.telefono, w.paciente.email,
                w.tipoAtencion, w.especialidad, w.prioridad, w.estado,
                w.fechaIngreso, w.fechaAsignacion
            )
            FROM WaitlistItemEntity w
            WHERE w.estado = :estado
            ORDER BY w.prioridad ASC, w.fechaIngreso ASC
            """)
    List<WaitlistItemDto> findDtoByEstadoOrderByPrioridad(@Param("estado") EstadoEspera estado);

    @Query("""
            SELECT new cl.rednorte.listaespera.infrastructure.adapter.output.persistence.dto.WaitlistItemDto(
                w.id,
                w.paciente.id, w.paciente.rut, w.paciente.nombre, w.paciente.apellido,
                w.paciente.telefono, w.paciente.email,
                w.tipoAtencion, w.especialidad, w.prioridad, w.estado,
                w.fechaIngreso, w.fechaAsignacion
            )
            FROM WaitlistItemEntity w
            WHERE w.especialidad = :especialidad AND w.estado = :estado
            ORDER BY w.prioridad ASC, w.fechaIngreso ASC
            """)
    List<WaitlistItemDto> findDtoByEspecialidadAndEstado(
            @Param("especialidad") String especialidad,
            @Param("estado") EstadoEspera estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT w
            FROM WaitlistItemEntity w
            JOIN FETCH w.paciente
            WHERE w.especialidad = :especialidad AND w.estado = :estado
            ORDER BY w.prioridad ASC, w.fechaIngreso ASC
            """)
    List<WaitlistItemEntity> findSiguientesDisponiblesForUpdate(
            @Param("especialidad") String especialidad,
            @Param("estado") EstadoEspera estado,
            Pageable pageable);
}
