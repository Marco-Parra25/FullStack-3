package org.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Reasignacion {
    @Id
    private UUID id;
    private String pacienteRut;
    private String especialidad;
    private LocalDateTime fechaAsignacion;
    private String estado;
    private UUID cupoOrigenId;

    public Reasignacion() {}

    public Reasignacion(UUID id, String pacienteRut, String especialidad, LocalDateTime fechaAsignacion, String estado, UUID cupoOrigenId) {
        this.id = id;
        this.pacienteRut = pacienteRut;
        this.especialidad = especialidad;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
        this.cupoOrigenId = cupoOrigenId;
    }

    // Getters
    public UUID getId() { return id; }
    public String getPacienteRut() { return pacienteRut; }
    public String getEspecialidad() { return especialidad; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public String getEstado() { return estado; }
    public UUID getCupoOrigenId() { return cupoOrigenId; }
}