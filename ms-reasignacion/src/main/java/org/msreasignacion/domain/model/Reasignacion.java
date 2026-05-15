package org.msreasignacion.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reasignacion {
    private UUID id;
    private String pacienteRut;
    private String especialidad;
    private LocalDateTime fechaAsignacion;
    private EstadoReasignacion estado;
    private UUID cupoOrigenId;

    public Reasignacion() {}

    public Reasignacion(
            UUID id,
            String pacienteRut,
            String especialidad,
            LocalDateTime fechaAsignacion,
            EstadoReasignacion estado,
            UUID cupoOrigenId
    ) {
        this.id = id;
        this.pacienteRut = pacienteRut;
        this.especialidad = especialidad;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
        this.cupoOrigenId = cupoOrigenId;
    }

    public static Reasignacion crearPendiente(
            String pacienteRut,
            String especialidad,
            UUID cupoOrigenId
    ) {
        return new Reasignacion(
                UUID.randomUUID(),
                pacienteRut,
                especialidad,
                LocalDateTime.now(),
                EstadoReasignacion.PENDIENTE,
                cupoOrigenId
        );
    }

    public void marcarAsignada() {
        this.estado = EstadoReasignacion.ASIGNADO;
    }

    public void marcarCompletada() {
        this.estado = EstadoReasignacion.COMPLETADO;
    }

    public void marcarFallida() {
        this.estado = EstadoReasignacion.FALLIDA;
    }

    public UUID getId() {
        return id;
    }

    public String getPacienteRut() {
        return pacienteRut;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public EstadoReasignacion getEstado() {
        return estado;
    }

    public UUID getCupoOrigenId() {
        return cupoOrigenId;
    }
}
