package org.msreasignacion.infrastructure.adapter.out.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.msreasignacion.domain.model.Paciente;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.msreasignacion.support.RedNorteRealTestData.CARDIOLOGIA;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_APELLIDO;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_EMAIL;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_ID;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_NOMBRE;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_RUT;
import static org.msreasignacion.support.RedNorteRealTestData.PACIENTE_MARIA_TELEFONO;
import static org.msreasignacion.support.RedNorteRealTestData.TRAUMATOLOGIA;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ListaEsperaClientAdapterTest {

    private static final String LISTA_ESPERA_BASE_URL = "http://ms-lista-espera:8081";
    private static final String INTERNAL_TOKEN = "dev-internal-token";

    private MockRestServiceServer server;
    private ListaEsperaClientAdapter adapter;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        adapter = new ListaEsperaClientAdapter(restTemplate, LISTA_ESPERA_BASE_URL, INTERNAL_TOKEN);
    }

    @Test
    void obtenerSiguientePaciente_ConRespuestaRealDeListaEspera_DebeMapearPaciente() {
        server.expect(requestTo(startsWith(LISTA_ESPERA_BASE_URL + "/api/v1/waitlist/asignaciones/siguiente")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Token", INTERNAL_TOKEN))
                .andExpect(queryParam("especialidad", CARDIOLOGIA))
                .andRespond(withSuccess(pacienteMariaJson(), MediaType.APPLICATION_JSON));

        Optional<Paciente> paciente = adapter.obtenerSiguientePaciente(CARDIOLOGIA);

        assertTrue(paciente.isPresent());
        assertEquals(PACIENTE_MARIA_ID, paciente.get().getId());
        assertEquals(PACIENTE_MARIA_RUT, paciente.get().getRut());
        assertEquals(PACIENTE_MARIA_NOMBRE, paciente.get().getNombre());
        assertEquals(PACIENTE_MARIA_APELLIDO, paciente.get().getApellido());
        assertEquals(PACIENTE_MARIA_TELEFONO, paciente.get().getTelefono());
        assertEquals(PACIENTE_MARIA_EMAIL, paciente.get().getEmail());
        server.verify();
    }

    @Test
    void obtenerSiguientePaciente_CuandoListaEsperaNoTieneDisponibles_DebeRetornarEmpty() {
        server.expect(requestTo(startsWith(LISTA_ESPERA_BASE_URL + "/api/v1/waitlist/asignaciones/siguiente")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Token", INTERNAL_TOKEN))
                .andExpect(queryParam("especialidad", TRAUMATOLOGIA))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<Paciente> paciente = adapter.obtenerSiguientePaciente(TRAUMATOLOGIA);

        assertTrue(paciente.isEmpty());
        server.verify();
    }

    @Test
    void obtenerSiguientePaciente_CuandoRespuestaNoTieneBody_DebeRetornarEmpty() {
        server.expect(requestTo(startsWith(LISTA_ESPERA_BASE_URL + "/api/v1/waitlist/asignaciones/siguiente")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Internal-Token", INTERNAL_TOKEN))
                .andExpect(queryParam("especialidad", CARDIOLOGIA))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        Optional<Paciente> paciente = adapter.obtenerSiguientePaciente(CARDIOLOGIA);

        assertTrue(paciente.isEmpty());
        server.verify();
    }

    @Test
    void fallbackObtenerPaciente_DebeRetornarEmpty() {
        Optional<Paciente> paciente = adapter.fallbackObtenerPaciente(CARDIOLOGIA, new RuntimeException("timeout"));

        assertTrue(paciente.isEmpty());
    }

    private String pacienteMariaJson() {
        return """
                {
                  "id": %d,
                  "rut": "%s",
                  "nombre": "%s",
                  "apellido": "%s",
                  "telefono": "%s",
                  "email": "%s"
                }
                """.formatted(
                PACIENTE_MARIA_ID,
                PACIENTE_MARIA_RUT,
                PACIENTE_MARIA_NOMBRE,
                PACIENTE_MARIA_APELLIDO,
                PACIENTE_MARIA_TELEFONO,
                PACIENTE_MARIA_EMAIL
        );
    }
}
