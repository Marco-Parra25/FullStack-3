package org.msreasignacion.infrastructure.adapter.out.db;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reasignaciones")
public class ReasignacionEntity {
    @Id
    private UUID id;
    private String pacienteRut;
    private String especialidad;
    private LocalDateTime fechaAsignacion;
    private String estado;
    private UUID cupoOrigenId;

    // JPA requiere un constructor vacío
    public ReasignacionEntity() {}

    public ReasignacionEntity(UUID id, String pacienteRut, String especialidad, LocalDateTime fechaAsignacion, String estado, UUID cupoOrigenId) {
        this.id = id;
        this.pacienteRut = pacienteRut;
        this.especialidad = especialidad;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
        this.cupoOrigenId = cupoOrigenId;
    }

    // Getters (Para que el Adapter pueda leer de aquí)
    public UUID getId() { return id; }
    public String getPacienteRut() { return pacienteRut; }
    public String getEspecialidad() { return especialidad; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public String getEstado() { return estado; }
    public UUID getCupoOrigenId() { return cupoOrigenId; }
}