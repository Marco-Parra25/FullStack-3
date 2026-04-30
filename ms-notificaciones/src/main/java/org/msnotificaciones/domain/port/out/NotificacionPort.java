package org.msnotificaciones.domain.port.out;

public interface NotificacionPort {
    void enviarEmail(String destinatario, String mensaje, String rut, String especialidad);
    void enviarSMS(String telefono, String mensaje, String rut, String especialidad);
}