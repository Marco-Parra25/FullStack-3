package cl.rednorte.listaespera.infrastructure.exception;

public class PacienteAlreadyExistsException extends RuntimeException {

    public PacienteAlreadyExistsException(String rut) {
        super("Ya existe un paciente registrado con RUT " + rut);
    }
}
