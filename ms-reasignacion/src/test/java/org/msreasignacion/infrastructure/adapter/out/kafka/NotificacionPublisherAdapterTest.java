package org.msreasignacion.infrastructure.adapter.out.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.msreasignacion.domain.event.CupoAsignadoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_EMAIL;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_TELEFONO;

@ExtendWith(MockitoExtension.class)
class NotificacionPublisherAdapterTest {

    private static final String TOPIC = "cupo-asignado";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private NotificacionPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NotificacionPublisherAdapter(kafkaTemplate, TOPIC, 1000);
    }

    @Test
    void publicarCupoAsignado_DebeEnviarEventoAKafkaConRutComoKey() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(eq(TOPIC), eq(PACIENTE_MARIA_RUT), any(CupoAsignadoEvent.class)))
                .thenReturn(future);

        adapter.publicarCupoAsignado(
                PACIENTE_MARIA_RUT,
                PACIENTE_MARIA_TELEFONO,
                PACIENTE_MARIA_EMAIL,
                CARDIOLOGIA
        );

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq(TOPIC), eq(PACIENTE_MARIA_RUT), eventCaptor.capture());

        CupoAsignadoEvent evento = assertInstanceOf(CupoAsignadoEvent.class, eventCaptor.getValue());
        assertEquals(PACIENTE_MARIA_RUT, evento.pacienteRut());
        assertEquals(PACIENTE_MARIA_TELEFONO, evento.telefono());
        assertEquals(PACIENTE_MARIA_EMAIL, evento.email());
        assertEquals(CARDIOLOGIA, evento.especialidad());
    }

    @Test
    void publicarCupoAsignado_CuandoKafkaFalla_DebeLanzarIllegalStateException() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("broker down"));
        when(kafkaTemplate.send(eq(TOPIC), eq(PACIENTE_MARIA_RUT), any(CupoAsignadoEvent.class)))
                .thenReturn(future);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                adapter.publicarCupoAsignado(
                        PACIENTE_MARIA_RUT,
                        PACIENTE_MARIA_TELEFONO,
                        PACIENTE_MARIA_EMAIL,
                        CARDIOLOGIA
                )
        );

        assertEquals("No se pudo publicar evento cupo-asignado", exception.getMessage());
    }
}
