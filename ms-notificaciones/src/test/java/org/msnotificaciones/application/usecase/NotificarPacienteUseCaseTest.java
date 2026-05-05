package org.msnotificaciones.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.domain.port.out.NotificacionPort;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificarPacienteUseCaseTest {

    @Mock
    private NotificacionPort notificacionPort; // Mockeamos la interfaz (puerto)

    @InjectMocks
    private NotificarPacienteUseCase useCase; // Inyectamos el mock en el caso de uso

    @Test
    void debeLlamarAPuertoDeNotificacionCuandoEventoEsValido() {
        // ARRANGE: Creamos el objeto de dominio (Evento)
        CupoAsignadoEvent evento = new CupoAsignadoEvent(
                "12345678-9",
                "paciente@correo.cl",
                "+56912345678",
                "Cardiología"
        );

        // ACT: Ejecutamos el caso de uso con el objeto completo
        useCase.ejecutar(evento);

        // ASSERT: Verificamos que el puerto recibió el objeto exacto
        // Como ahora el puerto recibe el EVENTO completo, el verify es mucho más limpio
        verify(notificacionPort, times(1)).enviarNotificacion(evento);
    }
}