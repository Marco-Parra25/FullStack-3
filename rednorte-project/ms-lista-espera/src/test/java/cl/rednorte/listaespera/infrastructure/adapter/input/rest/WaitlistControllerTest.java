package cl.rednorte.listaespera.infrastructure.adapter.input.rest;

import cl.rednorte.listaespera.domain.model.*;
import cl.rednorte.listaespera.domain.port.input.WaitlistUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WaitlistController.class)
class WaitlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaitlistUseCase waitlistUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private WaitlistItem crearItemDePrueba() {
        Paciente paciente = Paciente.builder()
                .id(1L).rut("12345678-9").nombre("María").apellido("González").build();

        return WaitlistItem.builder()
                .id(1L).paciente(paciente).tipoAtencion(TipoAtencion.CONSULTA)
                .especialidad("Cardiología").prioridad(3).estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.of(2025, 6, 15)).build();
    }

    @Test
    @DisplayName("POST /api/v1/waitlist registra paciente y retorna 201")
    void registrarPaciente() throws Exception {
        when(waitlistUseCase.registrarPaciente(eq(1L), eq(TipoAtencion.CONSULTA), eq("Cardiología")))
                .thenReturn(crearItemDePrueba());

        String body = """
                {"pacienteId": 1, "tipoAtencion": "CONSULTA", "especialidad": "Cardiología"}
                """;

        mockMvc.perform(post("/api/v1/waitlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoAtencion").value("CONSULTA"))
                .andExpect(jsonPath("$.prioridad").value(3))
                .andExpect(jsonPath("$.pacienteNombre").value("María González"));
    }

    @Test
    @DisplayName("GET /api/v1/waitlist retorna lista completa")
    void listarTodos() throws Exception {
        when(waitlistUseCase.listarTodos()).thenReturn(List.of(crearItemDePrueba()));

        mockMvc.perform(get("/api/v1/waitlist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].especialidad").value("Cardiología"));
    }

    @Test
    @DisplayName("GET /api/v1/waitlist/count retorna conteo")
    void contarEnEspera() throws Exception {
        when(waitlistUseCase.contarEnEspera()).thenReturn(42000L);

        mockMvc.perform(get("/api/v1/waitlist/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enEspera").value(42000));
    }

    @Test
    @DisplayName("PATCH /api/v1/waitlist/{id}/cancelar retorna 204")
    void cancelar() throws Exception {
        mockMvc.perform(patch("/api/v1/waitlist/1/cancelar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/waitlist/{id} retorna item específico")
    void obtenerPorId() throws Exception {
        when(waitlistUseCase.obtenerPorId(1L)).thenReturn(crearItemDePrueba());

        mockMvc.perform(get("/api/v1/waitlist/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pacienteRut").value("12345678-9"));
    }
}
