package org.application.usecase;

import org.domain.event.CupoLiberadoEvent;
import org.domain.model.Paciente;
import org.domain.model.Reasignacion;
import org.domain.port.out.EventoReasignacionPort;
import org.domain.port.out.PacientePort;
import org.infrastructure.repository.ReasignacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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
        // 1. ARRANGE: Preparamos los datos
        CupoLiberadoEvent evento = new CupoLiberadoEvent("Cardiología", UUID.randomUUID().toString());

        // ¡CORRECCIÓN AQUÍ! Usamos tu constructor con todos los parámetros
        Paciente paciente = new Paciente(
                1L,
                "12345678-9",
                "Juan",
                "Perez",
                "+56912345678",
                "paciente@correo.cl"
        );

        when(pacientePort.obtenerSiguientePaciente("Cardiología")).thenReturn(Optional.of(paciente));

        // 2. ACT: Ejecutamos el método
        useCase.ejecutar(evento);

        // 3. ASSERT: Verificamos las llamadas a la BD y a Kafka
        verify(repository, times(1)).save(any(Reasignacion.class));
        verify(eventoPort, times(1)).publicarCupoAsignado(
                eq("12345678-9"),
                eq("+56912345678"),
                eq("paciente@correo.cl"),
                eq("Cardiología")
        );
    }

    @Test
    void ejecutar_CuandoNoHayPaciente_NoDebeGuardarNiNotificar() {
        // 1. ARRANGE: Preparamos el escenario sin paciente
        CupoLiberadoEvent evento = new CupoLiberadoEvent("Traumatología", UUID.randomUUID().toString());
        when(pacientePort.obtenerSiguientePaciente("Traumatología")).thenReturn(Optional.empty());

        // 2. ACT: Ejecutamos el método
        useCase.ejecutar(evento);

        // 3. ASSERT: Verificamos que se detuvo en el "if" y no guardó nada
        verify(repository, never()).save(any(Reasignacion.class));
        verify(eventoPort, never()).publicarCupoAsignado(any(), any(), any(), any());
    }
}