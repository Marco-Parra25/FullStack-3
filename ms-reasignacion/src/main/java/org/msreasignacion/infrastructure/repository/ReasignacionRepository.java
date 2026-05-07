package org.msreasignacion.infrastructure.repository;

import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.infrastructure.adapter.out.db.ReasignacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReasignacionRepository extends JpaRepository<ReasignacionEntity, UUID> {
}