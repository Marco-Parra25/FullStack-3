package cl.rednorte.listaespera.infrastructure.exception;

public class PacienteNotFoundException extends RuntimeException {
    public PacienteNotFoundException(Long pacienteId) {
        super("Paciente con ID " + pacienteId + " no encontrado");
    }

    public PacienteNotFoundException(String message) {
        super(message);
    }
}
