package cl.rednorte.listaespera.infrastructure.adapter.output.persistence;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.domain.model.WaitlistItem;
import cl.rednorte.listaespera.domain.port.output.WaitlistRepository;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.PacienteEntity;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.WaitlistItemEntity;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.mapper.WaitlistItemMapper;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository.JpaPacienteRepository;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository.JpaWaitlistRepository;
import cl.rednorte.listaespera.infrastructure.exception.PacienteNotFoundException;
import cl.rednorte.listaespera.infrastructure.exception.WaitlistItemNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WaitlistPersistenceAdapter implements WaitlistRepository {

    private final JpaWaitlistRepository jpaWaitlistRepository;
    private final JpaPacienteRepository jpaPacienteRepository;

    public WaitlistPersistenceAdapter(JpaWaitlistRepository jpaWaitlistRepository,
                                      JpaPacienteRepository jpaPacienteRepository) {
        this.jpaWaitlistRepository = jpaWaitlistRepository;
        this.jpaPacienteRepository = jpaPacienteRepository;
    }

    @Override
    public WaitlistItem save(WaitlistItem item) {
        WaitlistItemEntity entity;

        if (item.getId() != null) {
            entity = jpaWaitlistRepository.findById(item.getId())
                    .orElseThrow(() -> new WaitlistItemNotFoundException(item.getId()));
            WaitlistItemMapper.toEntity(entity, item);
        } else {
            PacienteEntity pacienteEntity = jpaPacienteRepository.findById(item.getPaciente().getId())
                    .orElseThrow(() -> new PacienteNotFoundException(item.getPaciente().getId()));

            entity = WaitlistItemEntity.builder()
                    .paciente(pacienteEntity)
                    .tipoAtencion(item.getTipoAtencion())
                    .especialidad(item.getEspecialidad())
                    .prioridad(item.getPrioridad())
                    .estado(item.getEstado())
                    .fechaIngreso(item.getFechaIngreso())
                    .fechaAsignacion(item.getFechaAsignacion())
                    .build();
        }

        WaitlistItemEntity saved = jpaWaitlistRepository.save(entity);
        return WaitlistItemMapper.toDomain(saved);
    }

    @Override
    public Optional<WaitlistItem> findById(Long id) {
        // Usa JPQL con proyección al DTO de persistencia (un solo join en la query)
        return jpaWaitlistRepository.findDtoById(id)
                .map(WaitlistItemMapper::toDomain);
    }

    @Override
    public List<WaitlistItem> findAll() {
        // Usa JPQL con proyección al DTO de persistencia
        return jpaWaitlistRepository.findAllDto().stream()
                .map(WaitlistItemMapper::toDomain)
                .toList();
    }

    @Override
    public List<WaitlistItem> findByEspecialidad(String especialidad) {
        return jpaWaitlistRepository
                .findDtoByEspecialidadAndEstado(especialidad, EstadoEspera.EN_ESPERA)
                .stream()
                .map(WaitlistItemMapper::toDomain)
                .toList();
    }

    @Override
    public List<WaitlistItem> findAllOrderByPrioridad() {
        return jpaWaitlistRepository
                .findDtoByEstadoOrderByPrioridad(EstadoEspera.EN_ESPERA)
                .stream()
                .map(WaitlistItemMapper::toDomain)
                .toList();
    }

    @Override
    public long countEnEspera() {
        return jpaWaitlistRepository.countByEstado(EstadoEspera.EN_ESPERA);
    }
}
