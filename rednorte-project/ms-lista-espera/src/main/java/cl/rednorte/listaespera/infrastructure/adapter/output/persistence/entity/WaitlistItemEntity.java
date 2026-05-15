package cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.domain.model.TipoAtencion;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist_items")
public class WaitlistItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atencion", nullable = false, length = 20)
    private TipoAtencion tipoAtencion;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(nullable = false)
    private int prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEspera estado;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    public WaitlistItemEntity() {}

    public WaitlistItemEntity(Long id, PacienteEntity paciente, TipoAtencion tipoAtencion, String especialidad,
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
    public void setId(Long id) { this.id = id; }
    public PacienteEntity getPaciente() { return paciente; }
    public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }
    public TipoAtencion getTipoAtencion() { return tipoAtencion; }
    public void setTipoAtencion(TipoAtencion tipoAtencion) { this.tipoAtencion = tipoAtencion; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public EstadoEspera getEstado() { return estado; }
    public void setEstado(EstadoEspera estado) { this.estado = estado; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private PacienteEntity paciente;
        private TipoAtencion tipoAtencion;
        private String especialidad;
        private int prioridad;
        private EstadoEspera estado;
        private LocalDate fechaIngreso;
        private LocalDateTime fechaAsignacion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder paciente(PacienteEntity paciente) { this.paciente = paciente; return this; }
        public Builder tipoAtencion(TipoAtencion tipoAtencion) { this.tipoAtencion = tipoAtencion; return this; }
        public Builder especialidad(String especialidad) { this.especialidad = especialidad; return this; }
        public Builder prioridad(int prioridad) { this.prioridad = prioridad; return this; }
        public Builder estado(EstadoEspera estado) { this.estado = estado; return this; }
        public Builder fechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; return this; }
        public Builder fechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; return this; }
        public WaitlistItemEntity build() {
            return new WaitlistItemEntity(id, paciente, tipoAtencion, especialidad, prioridad, estado, fechaIngreso, fechaAsignacion);
        }
    }
}
