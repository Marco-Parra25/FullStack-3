package org.msreasignacion.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_FECHA;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.WAITLIST_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.cupoOrigenUuid;

class ReasignacionTest {

    @Test
    void debeCrearReasignacionConConstructorVacio() {
        Reasignacion reasignacion = new Reasignacion();
        assertNotNull(reasignacion);
    }

    @Test
    void debeCrearReasignacionConDatosYValidarGetters() {
        UUID idEsperado = REASIGNACION_CARDIOLOGIA_ID;
        String rutEsperado = PACIENTE_MARIA_RUT;
        String especialidadEsperada = CARDIOLOGIA;
        LocalDateTime fechaEsperada = REASIGNACION_CARDIOLOGIA_FECHA;
        EstadoReasignacion estadoEsperado = EstadoReasignacion.ASIGNADO;
        UUID cupoIdEsperado = cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID);

        Reasignacion reasignacion = new Reasignacion(
                idEsperado,
                rutEsperado,
                especialidadEsperada,
                fechaEsperada,
                estadoEsperado,
                cupoIdEsperado
        );

        assertNotNull(reasignacion);
        assertEquals(idEsperado, reasignacion.getId());
        assertEquals(rutEsperado, reasignacion.getPacienteRut());
        assertEquals(especialidadEsperada, reasignacion.getEspecialidad());
        assertEquals(fechaEsperada, reasignacion.getFechaAsignacion());
        assertEquals(estadoEsperado, reasignacion.getEstado());
        assertEquals(cupoIdEsperado, reasignacion.getCupoOrigenId());
    }

    @Test
    void crearPendiente_ConPacienteSemillaDeListaEspera_DebeIniciarConEstadoPendiente() {
        LocalDateTime antesDeCrear = LocalDateTime.now();
        UUID cupoId = cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID);

        Reasignacion reasignacion = Reasignacion.crearPendiente(
                PACIENTE_MARIA_RUT,
                CARDIOLOGIA,
                cupoId
        );
        LocalDateTime despuesDeCrear = LocalDateTime.now();

        assertNotNull(reasignacion.getId());
        assertEquals(PACIENTE_MARIA_RUT, reasignacion.getPacienteRut());
        assertEquals(CARDIOLOGIA, reasignacion.getEspecialidad());
        assertEquals(EstadoReasignacion.PENDIENTE, reasignacion.getEstado());
        assertEquals(cupoId, reasignacion.getCupoOrigenId());
        assertNotNull(reasignacion.getFechaAsignacion());
        assertFalse(reasignacion.getFechaAsignacion().isBefore(antesDeCrear));
        assertFalse(reasignacion.getFechaAsignacion().isAfter(despuesDeCrear));
    }

    @Test
    void cambiosDeEstado_DebenActualizarEstadoDeReasignacion() {
        Reasignacion reasignacion = Reasignacion.crearPendiente(
                PACIENTE_MARIA_RUT,
                CARDIOLOGIA,
                cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID)
        );

        reasignacion.marcarAsignada();
        assertEquals(EstadoReasignacion.ASIGNADO, reasignacion.getEstado());

        reasignacion.marcarCompletada();
        assertEquals(EstadoReasignacion.COMPLETADO, reasignacion.getEstado());

        reasignacion.marcarFallida();
        assertEquals(EstadoReasignacion.FALLIDA, reasignacion.getEstado());
    }
}
