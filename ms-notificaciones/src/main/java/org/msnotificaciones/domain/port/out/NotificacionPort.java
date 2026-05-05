package org.msnotificaciones.domain.port.out;

import org.msnotificaciones.domain.event.CupoAsignadoEvent;

public interface NotificacionPort {
    // El puerto ahora es genérico y recibe el evento completo
    void enviarNotificacion(CupoAsignadoEvent evento);
}