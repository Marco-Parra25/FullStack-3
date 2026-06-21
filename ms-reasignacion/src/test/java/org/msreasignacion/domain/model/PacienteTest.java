package org.msreasignacion.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_APELLIDO;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_EMAIL;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_NOMBRE;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_TELEFONO;

class PacienteTest {

    @Test
    void constructorVacio_DebeCrearPacienteParaJackson() {
        assertNotNull(new Paciente());
    }

    @Test
    void constructorConDatos_DebeExponerTodosLosGetters() {
        Paciente paciente = new Paciente(
                PACIENTE_MARIA_ID,
                PACIENTE_MARIA_RUT,
                PACIENTE_MARIA_NOMBRE,
                PACIENTE_MARIA_APELLIDO,
                PACIENTE_MARIA_TELEFONO,
                PACIENTE_MARIA_EMAIL
        );

        assertEquals(PACIENTE_MARIA_ID, paciente.getId());
        assertEquals(PACIENTE_MARIA_RUT, paciente.getRut());
        assertEquals(PACIENTE_MARIA_NOMBRE, paciente.getNombre());
        assertEquals(PACIENTE_MARIA_APELLIDO, paciente.getApellido());
        assertEquals(PACIENTE_MARIA_TELEFONO, paciente.getTelefono());
        assertEquals(PACIENTE_MARIA_EMAIL, paciente.getEmail());
    }
}
