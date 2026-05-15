package org.msreasignacion.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReasignacionTest {

    @Test
    void debeCrearReasignacionConConstructorVacio() {
        Reasignacion reasignacion = new Reasignacion();
        assertNotNull(reasignacion);
    }

    @Test
    void debeCrearReasignacionConDatosYValidarGetters() {
        UUID idEsperado = UUID.randomUUID();
        String rutEsperado = "12345678-9";
        String especialidadEsperada = "Cardiologia";
        LocalDateTime fechaEsperada = LocalDateTime.now();
        EstadoReasignacion estadoEsperado = EstadoReasignacion.ASIGNADO;
        UUID cupoIdEsperado = UUID.randomUUID();

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
    void crearPendiente_DebeIniciarConEstadoPendiente() {
        UUID cupoId = UUID.randomUUID();

        Reasignacion reasignacion = Reasignacion.crearPendiente(
                "12345678-9",
                "Cardiologia",
                cupoId
        );

        assertNotNull(reasignacion.getId());
        assertEquals("12345678-9", reasignacion.getPacienteRut());
        assertEquals("Cardiologia", reasignacion.getEspecialidad());
        assertEquals(EstadoReasignacion.PENDIENTE, reasignacion.getEstado());
        assertEquals(cupoId, reasignacion.getCupoOrigenId());
        assertNotNull(reasignacion.getFechaAsignacion());
    }

    @Test
    void cambiosDeEstado_DebenActualizarEstadoDeReasignacion() {
        Reasignacion reasignacion = Reasignacion.crearPendiente(
                "12345678-9",
                "Cardiologia",
                UUID.randomUUID()
        );

        reasignacion.marcarAsignada();
        assertEquals(EstadoReasignacion.ASIGNADO, reasignacion.getEstado());

        reasignacion.marcarCompletada();
        assertEquals(EstadoReasignacion.COMPLETADO, reasignacion.getEstado());

        reasignacion.marcarFallida();
        assertEquals(EstadoReasignacion.FALLIDA, reasignacion.getEstado());
    }
}
