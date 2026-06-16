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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_EMAIL;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_TELEFONO;
import static org.msreasignacion.support.RedNorteRealTestData.TRAUMATOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.WAITLIST_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.cupoLiberadoCardiologia;
import static org.msreasignacion.support.RedNorteRealTestData.cupoLiberadoTraumatologia;
import static org.msreasignacion.support.RedNorteRealTestData.cupoOrigenUuid;
import static org.msreasignacion.support.RedNorteRealTestData.pacienteMariaGonzalez;

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
    void ejecutar_ConPacienteRealDeListaEspera_DebeGuardarFlujoCompletoYNotificar() {
        CupoLiberadoEvent evento = cupoLiberadoCardiologia();
        Paciente paciente = pacienteMariaGonzalez();
        List<ReasignacionGuardada> reasignacionesGuardadas = capturarReasignacionesGuardadas();

        when(pacientePort.obtenerSiguientePaciente(CARDIOLOGIA)).thenReturn(Optional.of(paciente));

        useCase.ejecutar(evento);

        assertEquals(List.of(
                EstadoReasignacion.PENDIENTE,
                EstadoReasignacion.ASIGNADO,
                EstadoReasignacion.COMPLETADO
        ), estados(reasignacionesGuardadas));

        assertReasignacionesUsanDatosDeSemilla(reasignacionesGuardadas);

        verify(pacientePort, times(1)).obtenerSiguientePaciente(CARDIOLOGIA);
        verify(eventoPort, times(1)).publicarCupoAsignado(
                PACIENTE_MARIA_RUT,
                PACIENTE_MARIA_TELEFONO,
                PACIENTE_MARIA_EMAIL,
                CARDIOLOGIA
        );
    }

    @Test
    void ejecutar_CuandoNoHayPaciente_NoDebeGuardarNiNotificar() {
        CupoLiberadoEvent evento = cupoLiberadoTraumatologia();
        when(pacientePort.obtenerSiguientePaciente(TRAUMATOLOGIA)).thenReturn(Optional.empty());

        useCase.ejecutar(evento);

        verify(pacientePort, times(1)).obtenerSiguientePaciente(TRAUMATOLOGIA);
        verify(repository, never()).guardar(any(Reasignacion.class));
        verify(eventoPort, never()).publicarCupoAsignado(any(), any(), any(), any());
    }

    @Test
    void ejecutar_CuandoFallaNotificacionConPacienteReal_DebeGuardarEstadoFallida() {
        CupoLiberadoEvent evento = cupoLiberadoCardiologia();
        Paciente paciente = pacienteMariaGonzalez();
        List<ReasignacionGuardada> reasignacionesGuardadas = capturarReasignacionesGuardadas();

        when(pacientePort.obtenerSiguientePaciente(CARDIOLOGIA)).thenReturn(Optional.of(paciente));
        doThrow(new IllegalStateException("Kafka no disponible"))
                .when(eventoPort)
                .publicarCupoAsignado(PACIENTE_MARIA_RUT, PACIENTE_MARIA_TELEFONO, PACIENTE_MARIA_EMAIL, CARDIOLOGIA);

        useCase.ejecutar(evento);

        assertEquals(List.of(
                EstadoReasignacion.PENDIENTE,
                EstadoReasignacion.ASIGNADO,
                EstadoReasignacion.FALLIDA
        ), estados(reasignacionesGuardadas));

        assertReasignacionesUsanDatosDeSemilla(reasignacionesGuardadas);
    }

    private List<ReasignacionGuardada> capturarReasignacionesGuardadas() {
        List<ReasignacionGuardada> reasignacionesGuardadas = new ArrayList<>();

        doAnswer(invocation -> {
            Reasignacion reasignacion = invocation.getArgument(0);
            reasignacionesGuardadas.add(ReasignacionGuardada.desde(reasignacion));
            return null;
        }).when(repository).guardar(any(Reasignacion.class));

        return reasignacionesGuardadas;
    }

    private List<EstadoReasignacion> estados(List<ReasignacionGuardada> reasignacionesGuardadas) {
        return reasignacionesGuardadas.stream()
                .map(ReasignacionGuardada::estado)
                .toList();
    }

    private void assertReasignacionesUsanDatosDeSemilla(List<ReasignacionGuardada> reasignacionesGuardadas) {
        assertEquals(3, reasignacionesGuardadas.size());

        UUID idReasignacion = reasignacionesGuardadas.get(0).id();
        assertNotNull(idReasignacion);

        LocalDateTime fechaAsignacion = reasignacionesGuardadas.get(0).fechaAsignacion();
        assertNotNull(fechaAsignacion);

        assertTrue(reasignacionesGuardadas.stream().allMatch(reasignacion ->
                idReasignacion.equals(reasignacion.id())
                        && PACIENTE_MARIA_RUT.equals(reasignacion.pacienteRut())
                        && CARDIOLOGIA.equals(reasignacion.especialidad())
                        && cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID).equals(reasignacion.cupoOrigenId())
                        && fechaAsignacion.equals(reasignacion.fechaAsignacion())
        ));
    }

    private record ReasignacionGuardada(
            UUID id,
            String pacienteRut,
            String especialidad,
            LocalDateTime fechaAsignacion,
            EstadoReasignacion estado,
            UUID cupoOrigenId
    ) {
        private static ReasignacionGuardada desde(Reasignacion reasignacion) {
            return new ReasignacionGuardada(
                    reasignacion.getId(),
                    reasignacion.getPacienteRut(),
                    reasignacion.getEspecialidad(),
                    reasignacion.getFechaAsignacion(),
                    reasignacion.getEstado(),
                    reasignacion.getCupoOrigenId()
            );
        }
    }
}
