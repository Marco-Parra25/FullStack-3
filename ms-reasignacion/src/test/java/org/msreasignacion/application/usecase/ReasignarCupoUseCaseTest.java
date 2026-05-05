package org.msreasignacion.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.msreasignacion.domain.port.out.PacientePort;
import org.msreasignacion.domain.port.out.ReasignacionRepositoryPort; // IMPORTANTE: Cambiado a Port

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReasignarCupoUseCaseTest {

    @Mock
    private ReasignacionRepositoryPort repository; // Ahora mockeamos el PUERTO, no el JPA directo

    @Mock
    private PacientePort pacientePort;

    @Mock
    private EventoReasignacionPort eventoPort;

    @InjectMocks
    private ReasignarCupoUseCase useCase;

    @Test
    void ejecutar_CuandoHayPaciente_DebeGuardarYNotificar() {
        // 1. ARRANGE
        String cupoId = UUID.randomUUID().toString();
        CupoLiberadoEvent evento = new CupoLiberadoEvent(cupoId, "Cardiología");

        Paciente paciente = new Paciente(
                1L, "12345678-9", "Juan", "Perez", "+56912345678", "paciente@correo.cl"
        );

        when(pacientePort.obtenerSiguientePaciente(anyString()))
                .thenReturn(Optional.of(paciente));

        // 2. ACT
        useCase.ejecutar(evento);

        // 3. ASSERT
        // Ahora verificamos contra guardar() (el método del Port) en lugar de save()
        verify(repository, atLeastOnce()).guardar(any(Reasignacion.class));

        verify(eventoPort, times(1)).publicarCupoAsignado(
                eq("12345678-9"),
                eq("+56912345678"),
                eq("paciente@correo.cl"),
                anyString()
        );
    }

    @Test
    void ejecutar_CuandoNoHayPaciente_NoDebeGuardarNiNotificar() {
        // ARRANGE
        CupoLiberadoEvent evento = new CupoLiberadoEvent(UUID.randomUUID().toString(), "Traumatología");
        when(pacientePort.obtenerSiguientePaciente(anyString())).thenReturn(Optional.empty());

        // ACT
        useCase.ejecutar(evento);

        // ASSERT
        verify(repository, never()).guardar(any(Reasignacion.class));
        verify(eventoPort, never()).publicarCupoAsignado(any(), any(), any(), any());
    }
}