package cl.rednorte.listaespera.domain.model;

public class Paciente {

    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String rut, nombre, apellido, telefono, email;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder rut(String rut) { this.rut = rut; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder apellido(String apellido) { this.apellido = apellido; return this; }
        public Builder telefono(String telefono) { this.telefono = telefono; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Paciente build() { return new Paciente(id, rut, nombre, apellido, telefono, email); }
    }
}
