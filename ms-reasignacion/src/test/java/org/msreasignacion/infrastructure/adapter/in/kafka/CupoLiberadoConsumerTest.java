package org.msreasignacion.infrastructure.adapter.in.kafka;

import org.junit.jupiter.api.Test;
import org.msreasignacion.application.usecase.ReasignarCupoUseCase;
import org.msreasignacion.domain.event.CupoLiberadoEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.msreasignacion.support.RedNorteRealTestData.cupoLiberadoCardiologia;

class CupoLiberadoConsumerTest {

    @Test
    void consumirCupoLiberado_DebeDelegarEventoAlCasoDeUso() {
        ReasignarCupoUseCase useCase = mock(ReasignarCupoUseCase.class);
        CupoLiberadoConsumer consumer = new CupoLiberadoConsumer(useCase);
        CupoLiberadoEvent evento = cupoLiberadoCardiologia();

        consumer.consumirCupoLiberado(evento);

        verify(useCase).ejecutar(evento);
    }
}
