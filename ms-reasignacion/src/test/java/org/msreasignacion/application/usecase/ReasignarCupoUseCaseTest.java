package org.msreasignacion.application.usecase;

import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.msreasignacion.domain.port.out.PacientePort;
import org.msreasignacion.infrastructure.repository.ReasignacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReasignarCupoUseCaseTest {

    @Mock
    private ReasignacionRepository repository;

    @Mock
    private PacientePort pacientePort;

    @Mock
    private EventoReasignacionPort eventoPort;

    @InjectMocks
    private ReasignarCupoUseCase useCase;

    @Test
    void ejecutar_CuandoHayPaciente_DebeGuardarYNotificar() {
        // 1. ARRANGE
        // Generamos un UUID real para que UUID.fromString() en el UseCase no explote
        String especialidadId = UUID.randomUUID().toString();
        CupoLiberadoEvent evento = new CupoLiberadoEvent(especialidadId, "Cardiología");

        Paciente paciente = new Paciente(
                1L,
                "12345678-9",
                "Juan",
                "Perez",
                "+56912345678",
                "paciente@correo.cl"
        );

        // Usamos anyString() para que Mockito no se queje por coincidencia exacta
        when(pacientePort.obtenerSiguientePaciente(anyString()))
                .thenReturn(Optional.of(paciente));

        // 2. ACT
        useCase.ejecutar(evento);

        // 3. ASSERT
        verify(repository, times(1)).save(any(Reasignacion.class));
        verify(eventoPort, times(1)).publicarCupoAsignado(
                eq("12345678-9"),
                eq("+56912345678"),
                eq("paciente@correo.cl"),
                anyString()
        );
    }

    @Test
    void ejecutar_CuandoNoHayPaciente_NoDebeGuardarNiNotificar() {
        // 1. ARRANGE
        String especialidadId = UUID.randomUUID().toString();
        CupoLiberadoEvent evento = new CupoLiberadoEvent(especialidadId, "Traumatología");

        when(pacientePort.obtenerSiguientePaciente(anyString()))
                .thenReturn(Optional.empty());

        // 2. ACT
        useCase.ejecutar(evento);

        // 3. ASSERT
        verify(repository, never()).save(any(Reasignacion.class));
        verify(eventoPort, never()).publicarCupoAsignado(any(), any(), any(), any());
    }
}