package org.msnotificaciones.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msnotificaciones.domain.port.out.NotificacionPort;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificarPacienteUseCaseTest {

    @Mock
    private NotificacionPort notificacionPort;

    @InjectMocks
    private NotificarPacienteUseCase useCase;

    @Test
    void debeLlamarAEmailYSmsConDatosCorrectos() {
        // ARRANGE (Preparar datos)
        String rut = "12345678-9";
        String email = "paciente@correo.cl";
        String telefono = "+56912345678";
        String especialidad = "Cardiología";

        // ACT (Ejecutar)
        useCase.ejecutar(rut, email, telefono, especialidad);

        // ASSERT (Verificar)
        // Verificamos que se llame a enviarEmail con los 4 parámetros correctos
        verify(notificacionPort, times(1)).enviarEmail(
                eq(email),
                contains(rut), // El mensaje debe contener el RUT
                eq(rut),
                eq(especialidad)
        );

        // Verificamos que se llame a enviarSMS con los 4 parámetros correctos
        verify(notificacionPort, times(1)).enviarSMS(
                eq(telefono),
                contains(especialidad), // El mensaje debe contener la especialidad
                eq(rut),
                eq(especialidad)
        );
    }
}