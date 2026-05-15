package org.msreasignacion.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.EstadoReasignacion;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.domain.port.out.EventoReasignacionPort;
import org.msreasignacion.domain.port.out.PacientePort;
import org.msreasignacion.domain.port.out.ReasignacionRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReasignarCupoUseCaseTest {

    @Mock
    private ReasignacionRepositoryPort repository;

    @Mock
    private PacientePort pacientePort;

    @Mock
    private EventoReasignacionPort eventoPort;

    @InjectMocks
    private ReasignarCupoUseCase useCase;

    @Test
    void ejecutar_CuandoHayPaciente_DebeGuardarEstadosYNotificar() {
        CupoLiberadoEvent evento = new CupoLiberadoEvent(UUID.randomUUID().toString(), "Cardiologia");
        Paciente paciente = crearPaciente();
        List<EstadoReasignacion> estadosGuardados = capturarEstadosGuardados();

        when(pacientePort.obtenerSiguientePaciente(anyString())).thenReturn(Optional.of(paciente));

        useCase.ejecutar(evento);

        assertEquals(List.of(
                EstadoReasignacion.PENDIENTE,
                EstadoReasignacion.ASIGNADO,
                EstadoReasignacion.COMPLETADO
        ), estadosGuardados);

        verify(eventoPort, times(1)).publicarCupoAsignado(
                eq("12345678-9"),
                eq("+56912345678"),
                eq("paciente@correo.cl"),
                eq("Cardiologia")
        );
    }

    @Test
    void ejecutar_CuandoNoHayPaciente_NoDebeGuardarNiNotificar() {
        CupoLiberadoEvent evento = new CupoLiberadoEvent(UUID.randomUUID().toString(), "Traumatologia");
        when(pacientePort.obtenerSiguientePaciente(anyString())).thenReturn(Optional.empty());

        useCase.ejecutar(evento);

        verify(repository, never()).guardar(any(Reasignacion.class));
        verify(eventoPort, never()).publicarCupoAsignado(any(), any(), any(), any());
    }

    @Test
    void ejecutar_CuandoFallaNotificacion_DebeGuardarEstadoFallida() {
        CupoLiberadoEvent evento = new CupoLiberadoEvent(UUID.randomUUID().toString(), "Cardiologia");
        Paciente paciente = crearPaciente();
        List<EstadoReasignacion> estadosGuardados = capturarEstadosGuardados();

        when(pacientePort.obtenerSiguientePaciente(anyString())).thenReturn(Optional.of(paciente));
        doThrow(new IllegalStateException("Kafka no disponible"))
                .when(eventoPort)
                .publicarCupoAsignado(any(), any(), any(), any());

        useCase.ejecutar(evento);

        assertEquals(List.of(
                EstadoReasignacion.PENDIENTE,
                EstadoReasignacion.ASIGNADO,
                EstadoReasignacion.FALLIDA
        ), estadosGuardados);
    }

    private List<EstadoReasignacion> capturarEstadosGuardados() {
        List<EstadoReasignacion> estadosGuardados = new ArrayList<>();

        doAnswer(invocation -> {
            Reasignacion reasignacion = invocation.getArgument(0);
            estadosGuardados.add(reasignacion.getEstado());
            return null;
        }).when(repository).guardar(any(Reasignacion.class));

        return estadosGuardados;
    }

    private Paciente crearPaciente() {
        return new Paciente(
                1L,
                "12345678-9",
                "Juan",
                "Perez",
                "+56912345678",
                "paciente@correo.cl"
        );
    }
}
