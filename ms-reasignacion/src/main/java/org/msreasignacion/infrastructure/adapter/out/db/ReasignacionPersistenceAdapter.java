package org.msreasignacion.infrastructure.adapter.out.db;

import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.port.out.ReasignacionRepositoryPort;
import org.msreasignacion.infrastructure.repository.ReasignacionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ReasignacionPersistenceAdapter implements ReasignacionRepositoryPort {

    private final ReasignacionRepository jpaRepository;

    public ReasignacionPersistenceAdapter(ReasignacionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Reasignacion reasignacion) {
        // Convertimos el modelo de dominio a la entidad de base de datos
        ReasignacionEntity entity = new ReasignacionEntity(
                reasignacion.getId(),
                reasignacion.getPacienteRut(),
                reasignacion.getEspecialidad(),
                reasignacion.getFechaAsignacion(),
                reasignacion.getEstado(),
                reasignacion.getCupoOrigenId()
        );
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Reasignacion> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(entity -> new Reasignacion(
                        entity.getId(),
                        entity.getPacienteRut(),
                        entity.getEspecialidad(),
                        entity.getFechaAsignacion(),
                        entity.getEstado(),
                        entity.getCupoOrigenId()
                ));
    }
}