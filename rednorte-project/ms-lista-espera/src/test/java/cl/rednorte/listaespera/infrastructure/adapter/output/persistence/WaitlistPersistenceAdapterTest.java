package cl.rednorte.listaespera.infrastructure.adapter.output.persistence;

import cl.rednorte.listaespera.domain.model.*;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.PacienteEntity;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.entity.WaitlistItemEntity;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository.JpaPacienteRepository;
import cl.rednorte.listaespera.infrastructure.adapter.output.persistence.repository.JpaWaitlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class WaitlistPersistenceAdapterTest {

    @Autowired
    private JpaWaitlistRepository jpaWaitlistRepository;

    @Autowired
    private JpaPacienteRepository jpaPacienteRepository;

    private WaitlistPersistenceAdapter adapter;
    private PacienteEntity pacienteEntity;

    @BeforeEach
    void setUp() {
        adapter = new WaitlistPersistenceAdapter(jpaWaitlistRepository, jpaPacienteRepository);

        pacienteEntity = jpaPacienteRepository.save(PacienteEntity.builder()
                .rut("12345678-9").nombre("María").apellido("González")
                .telefono("+56912345678").email("maria@email.cl").build());
    }

    @Test
    @DisplayName("save persiste un nuevo WaitlistItem y retorna dominio con ID")
    void saveNuevo() {
        Paciente paciente = Paciente.builder().id(pacienteEntity.getId())
                .rut("12345678-9").nombre("María").apellido("González").build();

        WaitlistItem item = WaitlistItem.builder()
                .paciente(paciente).tipoAtencion(TipoAtencion.CONSULTA)
                .especialidad("Cardiología").prioridad(3).estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.now()).build();

        WaitlistItem saved = adapter.save(item);

        assertNotNull(saved.getId());
        assertEquals("Cardiología", saved.getEspecialidad());
        assertEquals(TipoAtencion.CONSULTA, saved.getTipoAtencion());
    }

    @Test
    @DisplayName("findByEspecialidad retorna items filtrados y ordenados")
    void findByEspecialidad() {
        crearItemEntity("Cardiología", TipoAtencion.CONSULTA, 3);
        crearItemEntity("Cardiología", TipoAtencion.URGENCIA_DIFERIDA, 1);
        crearItemEntity("Neurología", TipoAtencion.CONSULTA, 3);

        List<WaitlistItem> result = adapter.findByEspecialidad("Cardiología");

        assertEquals(2, result.size());
        assertTrue(result.get(0).getPrioridad() <= result.get(1).getPrioridad());
    }

    @Test
    @DisplayName("countEnEspera cuenta solo registros EN_ESPERA")
    void countEnEspera() {
        crearItemEntity("Cardiología", TipoAtencion.CONSULTA, 3);
        crearItemEntity("Neurología", TipoAtencion.CIRUGIA, 2);

        WaitlistItemEntity cancelado = crearItemEntity("Dermatología", TipoAtencion.CONSULTA, 3);
        cancelado.setEstado(EstadoEspera.CANCELADO);
        jpaWaitlistRepository.save(cancelado);

        assertEquals(2, adapter.countEnEspera());
    }

    private WaitlistItemEntity crearItemEntity(String especialidad, TipoAtencion tipo, int prioridad) {
        return jpaWaitlistRepository.save(WaitlistItemEntity.builder()
                .paciente(pacienteEntity).tipoAtencion(tipo).especialidad(especialidad)
                .prioridad(prioridad).estado(EstadoEspera.EN_ESPERA).fechaIngreso(LocalDate.now())
                .build());
    }
}
