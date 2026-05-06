package org.msnotificaciones.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.domain.port.out.NotificacionPort;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificarPacienteUseCaseTest {

    @Mock
    private NotificacionPort notificacionPort;

    @InjectMocks
    private NotificarPacienteUseCase useCase;

    @Test
    void debeEnviarEmailYSmsCuandoAmbosDatosEstanPresentes() {
        // ARRANGE
        CupoAsignadoEvent evento = new CupoAsignadoEvent(
                "12345678-9",
                "paciente@correo.cl",
                "+56912345678",
                "Cardiología"
        );

        // ACT
        useCase.ejecutar(evento);

        // ASSERT: Verificamos que se llamaron AMBOS canales
        verify(notificacionPort, times(1)).enviarEmail(eq("paciente@correo.cl"), contains("Cardiología"));
        verify(notificacionPort, times(1)).enviarSMS(eq("+56912345678"), contains("12345678-9"));
    }

    @Test
    void debeEnviarSoloEmailSiTelefonoEsNulo() {
        // ARRANGE
        CupoAsignadoEvent evento = new CupoAsignadoEvent(
                "12345678-9",
                "paciente@correo.cl",
                null, // Teléfono nulo
                "Cardiología"
        );

        // ACT
        useCase.ejecutar(evento);

        // ASSERT
        verify(notificacionPort, times(1)).enviarEmail(anyString(), anyString());
        verify(notificacionPort, never()).enviarSMS(anyString(), anyString()); // Verificamos la lógica de decisión
    }

    @Test
    void noDebeLlamarAPuertoSiRutEsNulo() {
        // ARRANGE
        CupoAsignadoEvent evento = new CupoAsignadoEvent(null, "p@c.cl", "123", "Cita");

        // ACT
        useCase.ejecutar(evento);

        // ASSERT: La validación de negocio debería detener el proceso
        verifyNoInteractions(notificacionPort);
    }
}