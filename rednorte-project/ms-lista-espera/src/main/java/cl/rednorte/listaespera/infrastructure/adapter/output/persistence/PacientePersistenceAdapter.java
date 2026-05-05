package cl.rednorte.listaespera.infrastructure.adapter.output.persistence;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.port.output.PacienteRepository;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.mapper.PacienteMapper;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository.JpaPacienteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PacientePersistenceAdapter implements PacienteRepository {

    private final JpaPacienteRepository jpaPacienteRepository;

    public PacientePersistenceAdapter(JpaPacienteRepository jpaPacienteRepository) {
        this.jpaPacienteRepository = jpaPacienteRepository;
    }

    @Override
    public Optional<Paciente> findById(Long id) {
        // Usa la consulta JPQL que proyecta directamente al DTO de persistencia
        return jpaPacienteRepository.findDtoById(id)
                .map(PacienteMapper::toDomain);
    }

    @Override
    public boolean existsByRut(String rut) {
        return jpaPacienteRepository.existsByRut(rut);
    }

    @Override
    public Paciente save(Paciente paciente) {
        // Para escritura se usa la entidad JPA directamente
        var entity = PacienteMapper.toEntity(paciente);
        var saved = jpaPacienteRepository.save(entity);
        return PacienteMapper.toDomain(saved);
    }

    @Override
    public List<Paciente> findAll() {
        // Usa la consulta JPQL que proyecta directamente al DTO de persistencia
        return jpaPacienteRepository.findAllDto().stream()
                .map(PacienteMapper::toDomain)
                .toList();
    }
}
