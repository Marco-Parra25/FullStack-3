package org.msreasignacion.infrastructure.repository;

import org.msreasignacion.domain.model.Reasignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReasignacionRepository extends JpaRepository<Reasignacion, UUID> {
}