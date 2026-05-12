package cl.rednorte.listaespera.infrastructure.adapter.input.rest;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.port.input.PacienteUseCase;
import cl.rednorte.listaespera.infrastructure.exception.PacienteAlreadyExistsException;
import cl.rednorte.listaespera.infrastructure.exception.PacienteNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PacienteController.class)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteUseCase pacienteUseCase;

    @Test
    @DisplayName("POST /api/v1/pacientes retorna 201 al registrar paciente")
    void registrarPaciente() throws Exception {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .rut("12345678-9")
                .nombre("Maria")
                .apellido("Gonzalez")
                .telefono("+56912345678")
                .email("maria@email.cl")
                .build();

        when(pacienteUseCase.registrar(any())).thenReturn(paciente);

        String body = """
                {"rut":"12345678-9","nombre":"Maria","apellido":"Gonzalez","telefono":"+56912345678","email":"maria@email.cl"}
                """;

        mockMvc.perform(post("/api/v1/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    @DisplayName("POST /api/v1/pacientes retorna 409 si el RUT ya existe")
    void registrarPacienteDuplicado() throws Exception {
        when(pacienteUseCase.registrar(any())).thenThrow(new PacienteAlreadyExistsException("12345678-9"));

        String body = """
                {"rut":"12345678-9","nombre":"Maria","apellido":"Gonzalez","telefono":"+56912345678","email":"maria@email.cl"}
                """;

        mockMvc.perform(post("/api/v1/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Paciente duplicado"));
    }

    @Test
    @DisplayName("GET /api/v1/pacientes/{id} retorna 404 si no existe")
    void obtenerPacienteNoEncontrado() throws Exception {
        when(pacienteUseCase.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pacientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Paciente no encontrado"));
    }

    @Test
    @DisplayName("GET /api/v1/pacientes/{id} retorna paciente si existe")
    void obtenerPaciente() throws Exception {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .rut("12345678-9")
                .nombre("Maria")
                .apellido("Gonzalez")
                .build();

        when(pacienteUseCase.obtenerPorId(1L)).thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/api/v1/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria"));
    }
}
