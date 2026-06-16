package org.msnotificaciones.infrastructure.adapter.in.kafka.dto;

public class CupoAsignadoDTO {
    private String pacienteRut;
    private String email;
    private String telefono;
    private String especialidad;

    public CupoAsignadoDTO() {
    }

    public CupoAsignadoDTO(String pacienteRut, String email, String telefono, String especialidad) {
        this.pacienteRut = pacienteRut;
        this.email = email;
        this.telefono = telefono;
        this.especialidad = especialidad;
    }

    public String getPacienteRut() {
        return pacienteRut;
    }

    public void setPacienteRut(String pacienteRut) {
        this.pacienteRut = pacienteRut;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
