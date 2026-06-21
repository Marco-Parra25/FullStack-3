package org.msreasignacion.infrastructure.adapter.out.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msreasignacion.domain.model.EstadoReasignacion;
import org.msreasignacion.domain.model.Reasignacion;
import org.msreasignacion.infrastructure.repository.ReasignacionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_FECHA;
import static org.msreasignacion.support.RedNorteRealTestData.REASIGNACION_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.WAITLIST_CARDIOLOGIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.cupoOrigenUuid;

@ExtendWith(MockitoExtension.class)
class ReasignacionPersistenceAdapterTest {

    @Mock
    private ReasignacionRepository jpaRepository;

    @InjectMocks
    private ReasignacionPersistenceAdapter adapter;

    @Test
    void guardar_DebeConvertirDominioAEntity() {
        Reasignacion reasignacion = reasignacionCompletada();

        adapter.guardar(reasignacion);

        ArgumentCaptor<ReasignacionEntity> captor = ArgumentCaptor.forClass(ReasignacionEntity.class);
        verify(jpaRepository).save(captor.capture());

        ReasignacionEntity entity = captor.getValue();
        assertEquals(REASIGNACION_CARDIOLOGIA_ID, entity.getId());
        assertEquals(PACIENTE_MARIA_RUT, entity.getPacienteRut());
        assertEquals(CARDIOLOGIA, entity.getEspecialidad());
        assertEquals(REASIGNACION_CARDIOLOGIA_FECHA, entity.getFechaAsignacion());
        assertEquals("COMPLETADO", entity.getEstado());
        assertEquals(cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID), entity.getCupoOrigenId());
    }

    @Test
    void buscarPorId_CuandoExiste_DebeConvertirEntityADominio() {
        ReasignacionEntity entity = entityCompletada();
        when(jpaRepository.findById(REASIGNACION_CARDIOLOGIA_ID)).thenReturn(Optional.of(entity));

        Optional<Reasignacion> resultado = adapter.buscarPorId(REASIGNACION_CARDIOLOGIA_ID);

        assertTrue(resultado.isPresent());
        Reasignacion reasignacion = resultado.get();
        assertEquals(REASIGNACION_CARDIOLOGIA_ID, reasignacion.getId());
        assertEquals(PACIENTE_MARIA_RUT, reasignacion.getPacienteRut());
        assertEquals(CARDIOLOGIA, reasignacion.getEspecialidad());
        assertEquals(REASIGNACION_CARDIOLOGIA_FECHA, reasignacion.getFechaAsignacion());
        assertEquals(EstadoReasignacion.COMPLETADO, reasignacion.getEstado());
        assertEquals(cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID), reasignacion.getCupoOrigenId());
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeRetornarEmpty() {
        UUID id = UUID.fromString("33333333-3333-4333-8333-333333333333");
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Reasignacion> resultado = adapter.buscarPorId(id);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void entity_ConstructorVacioDebeCrearInstanciaJpa() {
        assertNotNull(new ReasignacionEntity());
    }

    private Reasignacion reasignacionCompletada() {
        return new Reasignacion(
                REASIGNACION_CARDIOLOGIA_ID,
                PACIENTE_MARIA_RUT,
                CARDIOLOGIA,
                REASIGNACION_CARDIOLOGIA_FECHA,
                EstadoReasignacion.COMPLETADO,
                cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID)
        );
    }

    private ReasignacionEntity entityCompletada() {
        return new ReasignacionEntity(
                REASIGNACION_CARDIOLOGIA_ID,
                PACIENTE_MARIA_RUT,
                CARDIOLOGIA,
                REASIGNACION_CARDIOLOGIA_FECHA,
                "COMPLETADO",
                cupoOrigenUuid(WAITLIST_CARDIOLOGIA_ID)
        );
    }
}
