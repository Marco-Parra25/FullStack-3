package org.msreasignacion.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reasignacion {
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

    // Setters
    public void setEstado(String estado) { this.estado = estado; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
}