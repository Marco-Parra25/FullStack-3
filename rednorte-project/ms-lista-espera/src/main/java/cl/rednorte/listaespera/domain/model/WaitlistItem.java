package cl.rednorte.listaespera.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WaitlistItem {

    private Long id;
    private Paciente paciente;
    private TipoAtencion tipoAtencion;
    private String especialidad;
    private int prioridad;
    private EstadoEspera estado;
    private LocalDate fechaIngreso;
    private LocalDateTime fechaAsignacion;

    public WaitlistItem(Long id, Paciente paciente, TipoAtencion tipoAtencion, String especialidad,
                        int prioridad, EstadoEspera estado, LocalDate fechaIngreso, LocalDateTime fechaAsignacion) {
        this.id = id;
        this.paciente = paciente;
        this.tipoAtencion = tipoAtencion;
        this.especialidad = especialidad;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
        this.fechaAsignacion = fechaAsignacion;
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public TipoAtencion getTipoAtencion() { return tipoAtencion; }
    public String getEspecialidad() { return especialidad; }
    public int getPrioridad() { return prioridad; }
    public EstadoEspera getEstado() { return estado; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }

    public boolean isActivo() {
        return estado == EstadoEspera.EN_ESPERA;
    }

    public void cancelar() {
        this.estado = EstadoEspera.CANCELADO;
    }

    public void asignar() {
        this.estado = EstadoEspera.ASIGNADO;
        this.fechaAsignacion = LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Paciente paciente;
        private TipoAtencion tipoAtencion;
        private String especialidad;
        private int prioridad;
        private EstadoEspera estado;
        private LocalDate fechaIngreso;
        private LocalDateTime fechaAsignacion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder paciente(Paciente paciente) { this.paciente = paciente; return this; }
        public Builder tipoAtencion(TipoAtencion tipoAtencion) { this.tipoAtencion = tipoAtencion; return this; }
        public Builder especialidad(String especialidad) { this.especialidad = especialidad; return this; }
        public Builder prioridad(int prioridad) { this.prioridad = prioridad; return this; }
        public Builder estado(EstadoEspera estado) { this.estado = estado; return this; }
        public Builder fechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; return this; }
        public Builder fechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; return this; }
        public WaitlistItem build() {
            return new WaitlistItem(id, paciente, tipoAtencion, especialidad, prioridad, estado, fechaIngreso, fechaAsignacion);
        }
    }
}
