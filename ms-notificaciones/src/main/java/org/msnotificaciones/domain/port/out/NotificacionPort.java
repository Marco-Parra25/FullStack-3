package org.msnotificaciones.domain.port.out;

public interface NotificacionPort {
    void enviarEmail(String email, String mensaje);
    void enviarSMS(String telefono, String mensaje);
}