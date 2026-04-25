package cl.rednorte.listaespera.domain.factory;

import cl.rednorte.listaespera.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WaitlistItemFactoryTest {

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = Paciente.builder()
                .id(1L)
                .rut("12345678-9")
                .nombre("María")
                .apellido("González")
                .telefono("+56912345678")
                .email("maria@email.cl")
                .build();
    }

    @Test
    @DisplayName("Factory crea CONSULTA con prioridad 3 (FIFO)")
    void crearConsulta() {
        WaitlistItem item = WaitlistItemFactory.create(TipoAtencion.CONSULTA, paciente, "Cardiología");

        assertEquals(TipoAtencion.CONSULTA, item.getTipoAtencion());
        assertEquals(3, item.getPrioridad());
        assertEquals(EstadoEspera.EN_ESPERA, item.getEstado());
        assertEquals("Cardiología", item.getEspecialidad());
        assertEquals(LocalDate.now(), item.getFechaIngreso());
        assertNull(item.getFechaAsignacion());
    }

    @Test
    @DisplayName("Factory crea CIRUGÍA con prioridad 2")
    void crearCirugia() {
        WaitlistItem item = WaitlistItemFactory.create(TipoAtencion.CIRUGIA, paciente, "Traumatología");

        assertEquals(TipoAtencion.CIRUGIA, item.getTipoAtencion());
        assertEquals(2, item.getPrioridad());
        assertEquals(EstadoEspera.EN_ESPERA, item.getEstado());
    }

    @Test
    @DisplayName("Factory crea URGENCIA_DIFERIDA con prioridad 1 (máxima)")
    void crearUrgenciaDiferida() {
        WaitlistItem item = WaitlistItemFactory.create(TipoAtencion.URGENCIA_DIFERIDA, paciente, "Neurología");

        assertEquals(TipoAtencion.URGENCIA_DIFERIDA, item.getTipoAtencion());
        assertEquals(1, item.getPrioridad());
        assertEquals(EstadoEspera.EN_ESPERA, item.getEstado());
    }

    @Test
    @DisplayName("Factory asocia correctamente el paciente al item")
    void pacienteAsociado() {
        WaitlistItem item = WaitlistItemFactory.create(TipoAtencion.CONSULTA, paciente, "Dermatología");

        assertNotNull(item.getPaciente());
        assertEquals("12345678-9", item.getPaciente().getRut());
        assertEquals("María", item.getPaciente().getNombre());
    }

    @Test
    @DisplayName("Todos los tipos de atención generan estado EN_ESPERA")
    void todosEstadoEnEspera() {
        for (TipoAtencion tipo : TipoAtencion.values()) {
            WaitlistItem item = WaitlistItemFactory.create(tipo, paciente, "General");
            assertEquals(EstadoEspera.EN_ESPERA, item.getEstado(),
                    "Tipo " + tipo + " debería tener estado EN_ESPERA");
        }
    }
}
