package org.msreasignacion.domain.model;

public class Paciente {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    // Constructor vacío necesario para que Jackson/RestTemplate pueda convertir el JSON
    public Paciente() {}

    public Paciente(Long id, String rut, String nombre, String apellido, String telefono, String email) {
        this.id = id;
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getRut() { return rut; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
}