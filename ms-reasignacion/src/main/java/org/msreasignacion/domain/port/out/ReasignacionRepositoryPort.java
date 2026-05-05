package org.msreasignacion.domain.port.out;

import org.msreasignacion.domain.model.Reasignacion;
import java.util.Optional;
import java.util.UUID;

public interface ReasignacionRepositoryPort {
    void guardar(Reasignacion reasignacion);
    Optional<Reasignacion> buscarPorId(UUID id);
}