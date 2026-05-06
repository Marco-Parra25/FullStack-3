package org.msnotificaciones.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.domain.port.out.NotificacionPort;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificarPacienteUseCaseTest {

    @Mock
    private NotificacionPort notificacionPort;

    @InjectMocks
    private NotificarPacienteUseCase useCase;

    @Test
    void debeEnviarEmailYSmsCuandoAmbosDatosEstanPresentes() {
        // ARRANGE: Siguiendo tu record (RUT, TELÉFONO, EMAIL, ESPECIALIDAD)
        CupoAsignadoEvent evento = new CupoAsignadoEvent(
                "12345678-9",          // pacienteRut
                "+56912345678",        // telefono
                "paciente@correo.cl",  // email
                "Cardiología"          // especialidad
        );

        // ACT
        useCase.ejecutar(evento);

        // ASSERT
        // Ahora los verificadores coincidirán con los getters del record
        verify(notificacionPort, times(1)).enviarEmail(eq("paciente@correo.cl"), contains("Cardiología"));
        verify(notificacionPort, times(1)).enviarSMS(eq("+56912345678"), contains("12345678-9"));
    }

    @Test
    void debeEnviarSoloEmailSiTelefonoEsNulo() {
        // ARRANGE: Teléfono en posición 2 como null
        CupoAsignadoEvent evento = new CupoAsignadoEvent(
                "12345678-9",
                null,
                "paciente@correo.cl",
                "Cardiología"
        );

        // ACT
        useCase.ejecutar(evento);

        // ASSERT
        verify(notificacionPort, times(1)).enviarEmail(eq("paciente@correo.cl"), anyString());
        verify(notificacionPort, never()).enviarSMS(anyString(), anyString());
    }

    @Test
    void noDebeLlamarAPuertoSiRutEsNulo() {
        // ARRANGE
        CupoAsignadoEvent evento = new CupoAsignadoEvent(null, "123456", "p@c.cl", "Cita");

        // ACT
        useCase.ejecutar(evento);

        // ASSERT
        verifyNoInteractions(notificacionPort);
    }
}