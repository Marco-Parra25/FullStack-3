package cl.rednorte.listaespera.domain.service;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.port.output.PacienteRepository;
import cl.rednorte.listaespera.infrastructure.exception.PacienteAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    private PacienteService service;

    @BeforeEach
    void setUp() {
        service = new PacienteService(pacienteRepository);
    }

    @Test
    @DisplayName("Registrar paciente con RUT duplicado lanza conflicto")
    void registrarPacienteDuplicado() {
        Paciente paciente = Paciente.builder()
                .rut("12345678-9")
                .nombre("Maria")
                .apellido("Gonzalez")
                .build();

        when(pacienteRepository.existsByRut("12345678-9")).thenReturn(true);

        assertThrows(PacienteAlreadyExistsException.class, () -> service.registrar(paciente));
        verify(pacienteRepository, never()).save(paciente);
    }

    @Test
    @DisplayName("Registrar paciente nuevo persiste correctamente")
    void registrarPacienteNuevo() {
        Paciente paciente = Paciente.builder()
                .rut("12345678-9")
                .nombre("Maria")
                .apellido("Gonzalez")
                .build();

        Paciente guardado = Paciente.builder()
                .id(1L)
                .rut("12345678-9")
                .nombre("Maria")
                .apellido("Gonzalez")
                .build();

        when(pacienteRepository.existsByRut("12345678-9")).thenReturn(false);
        when(pacienteRepository.save(paciente)).thenReturn(guardado);

        Paciente result = service.registrar(paciente);

        assertEquals(1L, result.getId());
        verify(pacienteRepository).save(paciente);
    }
}
