package org.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReasignacionTest {

    @Test
    void debeCrearReasignacionConConstructorVacio() {
        // JPA e Hibernate necesitan un constructor vacío. Lo probamos aquí.
        Reasignacion reasignacion = new Reasignacion();
        assertNotNull(reasignacion);
    }

    @Test
    void debeCrearReasignacionConDatosYValidarGetters() {
        // 1. ARRANGE: Preparamos los datos exactos
        UUID idEsperado = UUID.randomUUID();
        String rutEsperado = "12345678-9";
        String especialidadEsperada = "Cardiología";
        LocalDateTime fechaEsperada = LocalDateTime.now();
        String estadoEsperado = "ASIGNADO";
        UUID cupoIdEsperado = UUID.randomUUID();

        // 2. ACT: Instanciamos el objeto con tu constructor
        Reasignacion reasignacion = new Reasignacion(
                idEsperado,
                rutEsperado,
                especialidadEsperada,
                fechaEsperada,
                estadoEsperado,
                cupoIdEsperado
        );

        // 3. ASSERT: Validamos que tus getters devuelven la información correcta
        assertNotNull(reasignacion);
        assertEquals(idEsperado, reasignacion.getId());
        assertEquals(rutEsperado, reasignacion.getPacienteRut());
        assertEquals(especialidadEsperada, reasignacion.getEspecialidad());
        assertEquals(fechaEsperada, reasignacion.getFechaAsignacion());
        assertEquals(estadoEsperado, reasignacion.getEstado());
        assertEquals(cupoIdEsperado, reasignacion.getCupoOrigenId());
    }
}