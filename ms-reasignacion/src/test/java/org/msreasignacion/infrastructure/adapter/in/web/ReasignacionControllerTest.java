package org.msreasignacion.infrastructure.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msreasignacion.application.usecase.ReasignarCupoUseCase;
import org.msreasignacion.domain.dto.ReasignacionDTO;
import org.msreasignacion.domain.model.EstadoReasignacion;
import org.msreasignacion.domain.model.Reasignacion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_FECHA;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.WAITLIST_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.cupoOrigenUuid;

@ExtendWith(MockitoExtension.class)
class ReasignacionControllerTest {

    @Mock
    private ReasignarCupoUseCase useCase;

    @InjectMocks
    private ReasignacionController controller;

    @Test
    void consultar_CuandoExisteReasignacion_DebeRetornarOkConDto() {
        Reasignacion reasignacion = new Reasignacion(
                REASIGNACION_CARDIOLOGIA_ID,
                PACIENTE_MARIA_RUT,
                CARDIOLOGIA,
                REASIGNACION_CARDIOLOGIA_FECHA,
                EstadoReasignacion.COMPLETADO,
                cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID)
        );
        when(useCase.obtenerDetalle(REASIGNACION_CARDIOLOGIA_ID)).thenReturn(Optional.of(reasignacion));

        ResponseEntity<ReasignacionDTO> response = controller.consultar(REASIGNACION_CARDIOLOGIA_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(REASIGNACION_CARDIOLOGIA_ID, response.getBody().id());
        assertEquals(PACIENTE_MARIA_RUT, response.getBody().pacienteRut());
        assertEquals(CARDIOLOGIA, response.getBody().especialidad());
        assertEquals("COMPLETADO", response.getBody().estado());
        assertEquals(REASIGNACION_CARDIOLOGIA_FECHA, response.getBody().fechaAsignacion());
        verify(useCase).obtenerDetalle(REASIGNACION_CARDIOLOGIA_ID);
    }

    @Test
    void consultar_CuandoNoExisteReasignacion_DebeRetornarNotFound() {
        UUID id = UUID.fromString("22222222-2222-4222-8222-222222222222");
        when(useCase.obtenerDetalle(id)).thenReturn(Optional.empty());

        ResponseEntity<ReasignacionDTO> response = controller.consultar(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(useCase).obtenerDetalle(id);
    }
}
