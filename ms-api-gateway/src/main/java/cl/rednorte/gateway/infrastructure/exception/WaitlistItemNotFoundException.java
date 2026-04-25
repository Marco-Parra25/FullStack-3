package cl.rednorte.listaespera.infrastructure.exception;

public class WaitlistItemNotFoundException extends RuntimeException {

    public WaitlistItemNotFoundException(Long waitlistItemId) {
        super("Registro de lista de espera con ID " + waitlistItemId + " no encontrado");
    }
}
