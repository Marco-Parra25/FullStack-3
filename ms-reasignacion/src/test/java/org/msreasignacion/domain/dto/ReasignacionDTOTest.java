package org.msreasignacion.domain.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_FECHA;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_ID;

class ReasignacionDTOTest {

    @Test
    void debeExponerCamposDelRecord() {
        ReasignacionDTO dto = new ReasignacionDTO(
                REASIGNACION_CARDIOLOGIA_ID,
                PACIENTE_MARIA_RUT,
                CARDIOLOGIA,
                "COMPLETADO",
                REASIGNACION_CARDIOLOGIA_FECHA
        );

        assertEquals(REASIGNACION_CARDIOLOGIA_ID, dto.id());
        assertEquals(PACIENTE_MARIA_RUT, dto.pacienteRut());
        assertEquals(CARDIOLOGIA, dto.especialidad());
        assertEquals("COMPLETADO", dto.estado());
        assertEquals(REASIGNACION_CARDIOLOGIA_FECHA, dto.fechaAsignacion());
    }
}
