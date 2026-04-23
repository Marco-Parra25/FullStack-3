package cl.rednorte.listaespera.domain.service;

import cl.rednorte.listaespera.domain.model.*;
import cl.rednorte.listaespera.domain.port.output.PacienteRepository;
import cl.rednorte.listaespera.domain.port.output.WaitlistRepository;
import cl.rednorte.listaespera.infrastructure.exception.PacienteNotFoundException;
import cl.rednorte.listaespera.infrastructure.exception.WaitlistItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitlistServiceTest {

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    private WaitlistService service;
    private Paciente paciente;

    @BeforeEach
    void setUp() {
        service = new WaitlistService(waitlistRepository, pacienteRepository);
        paciente = Paciente.builder()
                .id(1L)
                .rut("12345678-9")
                .nombre("María")
                .apellido("González")
                .build();
    }

    @Test
    @DisplayName("Registrar paciente con CONSULTA usa Factory y persiste")
    void registrarPacienteConsulta() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(waitlistRepository.save(any())).thenAnswer(inv -> {
            WaitlistItem item = inv.getArgument(0);
            return WaitlistItem.builder()
                    .id(100L)
                    .paciente(item.getPaciente())
                    .tipoAtencion(item.getTipoAtencion())
                    .especialidad(item.getEspecialidad())
                    .prioridad(item.getPrioridad())
                    .estado(item.getEstado())
                    .fechaIngreso(item.getFechaIngreso())
                    .build();
        });

        WaitlistItem result = service.registrarPaciente(1L, TipoAtencion.CONSULTA, "Cardiología");

        assertNotNull(result.getId());
        assertEquals(TipoAtencion.CONSULTA, result.getTipoAtencion());
        assertEquals(3, result.getPrioridad());
        assertEquals("Cardiología", result.getEspecialidad());
        verify(waitlistRepository).save(any());
    }

    @Test
    @DisplayName("Registrar paciente inexistente lanza excepción")
    void registrarPacienteInexistente() {
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PacienteNotFoundException.class,
                () -> service.registrarPaciente(999L, TipoAtencion.CONSULTA, "Cardiología"));

        verify(waitlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelar cambia estado a CANCELADO")
    void cancelarItem() {
        WaitlistItem item = WaitlistItem.builder()
                .id(1L)
                .paciente(paciente)
                .tipoAtencion(TipoAtencion.CONSULTA)
                .especialidad("Cardiología")
                .prioridad(3)
                .estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.now())
                .build();

        when(waitlistRepository.findById(1L)).thenReturn(Optional.of(item));
        when(waitlistRepository.save(any())).thenReturn(item);

        service.cancelar(1L);

        assertEquals(EstadoEspera.CANCELADO, item.getEstado());
        verify(waitlistRepository).save(item);
    }

    @Test
    @DisplayName("Cancelar item inexistente lanza excepción")
    void cancelarInexistente() {
        when(waitlistRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(WaitlistItemNotFoundException.class, () -> service.cancelar(999L));
    }

    @Test
    @DisplayName("Listar por especialidad delega al repository")
    void listarPorEspecialidad() {
        WaitlistItem item = WaitlistItem.builder()
                .id(1L).paciente(paciente).tipoAtencion(TipoAtencion.CONSULTA)
                .especialidad("Cardiología").prioridad(3).estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.now()).build();

        when(waitlistRepository.findByEspecialidad("Cardiología")).thenReturn(List.of(item));

        List<WaitlistItem> result = service.listarPorEspecialidad("Cardiología");

        assertEquals(1, result.size());
        assertEquals("Cardiología", result.get(0).getEspecialidad());
    }

    @Test
    @DisplayName("Contar en espera delega al repository")
    void contarEnEspera() {
        when(waitlistRepository.countEnEspera()).thenReturn(42000L);

        assertEquals(42000L, service.contarEnEspera());
    }

    @Test
    @DisplayName("Registrar URGENCIA_DIFERIDA asigna prioridad 1")
    void registrarUrgenciaDiferida() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(waitlistRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WaitlistItem result = service.registrarPaciente(1L, TipoAtencion.URGENCIA_DIFERIDA, "Neurología");

        assertEquals(1, result.getPrioridad());
        assertEquals(TipoAtencion.URGENCIA_DIFERIDA, result.getTipoAtencion());
    }

    @Test
    @DisplayName("Registrar CIRUGÍA asigna prioridad 2")
    void registrarCirugia() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(waitlistRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WaitlistItem result = service.registrarPaciente(1L, TipoAtencion.CIRUGIA, "Traumatología");

        assertEquals(2, result.getPrioridad());
        assertEquals(TipoAtencion.CIRUGIA, result.getTipoAtencion());
    }
}
