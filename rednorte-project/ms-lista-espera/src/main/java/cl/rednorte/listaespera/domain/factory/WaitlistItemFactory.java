package cl.rednorte.listaespera.domain.factory;

import cl.rednorte.listaespera.domain.model.EstadoEspera;
import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.model.TipoAtencion;
import cl.rednorte.listaespera.domain.model.WaitlistItem;

import java.time.LocalDate;

public class WaitlistItemFactory {

    private WaitlistItemFactory() {}

    public static WaitlistItem create(TipoAtencion tipo, Paciente paciente, String especialidad) {
        return switch (tipo) {
            case CONSULTA -> crearConsulta(paciente, especialidad);
            case CIRUGIA -> crearCirugia(paciente, especialidad);
            case URGENCIA_DIFERIDA -> crearUrgenciaDiferida(paciente, especialidad);
        };
    }

    /**
     * Consulta ambulatoria: prioridad base 3 (FIFO con prioridad clínica).
     */
    private static WaitlistItem crearConsulta(Paciente paciente, String especialidad) {
        return WaitlistItem.builder()
                .paciente(paciente)
                .tipoAtencion(TipoAtencion.CONSULTA)
                .especialidad(especialidad)
                .prioridad(3)
                .estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.now())
                .build();
    }

    /**
     * Cirugía: prioridad base 2 (requiere preparación preoperatoria).
     */
    private static WaitlistItem crearCirugia(Paciente paciente, String especialidad) {
        return WaitlistItem.builder()
                .paciente(paciente)
                .tipoAtencion(TipoAtencion.CIRUGIA)
                .especialidad(especialidad)
                .prioridad(2)
                .estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.now())
                .build();
    }

    /**
     * Urgencia diferida: prioridad base 1 (máxima, plazo máximo de espera).
     */
    private static WaitlistItem crearUrgenciaDiferida(Paciente paciente, String especialidad) {
        return WaitlistItem.builder()
                .paciente(paciente)
                .tipoAtencion(TipoAtencion.URGENCIA_DIFERIDA)
                .especialidad(especialidad)
                .prioridad(1)
                .estado(EstadoEspera.EN_ESPERA)
                .fechaIngreso(LocalDate.now())
                .build();
    }
}
