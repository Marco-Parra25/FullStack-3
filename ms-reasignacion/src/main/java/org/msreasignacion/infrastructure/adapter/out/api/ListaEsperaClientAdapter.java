package org.msreasignacion.infrastructure.adapter.out.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.port.out.PacientePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class ListaEsperaClientAdapter implements PacientePort {

    private static final Logger log = LoggerFactory.getLogger(ListaEsperaClientAdapter.class);
    private final RestTemplate restTemplate;

    public ListaEsperaClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "listaEsperaCB", fallbackMethod = "fallbackObtenerPaciente")
    public Optional<Paciente> obtenerSiguientePaciente(String especialidad) {
        String url = "http://ms-lista-espera/api/pacientes/siguiente?especialidad=" + especialidad;
        log.info("Llamando al MS Lista de Espera para especialidad: {}", especialidad);

        Paciente paciente = restTemplate.getForObject(url, Paciente.class);
        return Optional.ofNullable(paciente);
    }

    // Este método se ejecuta si el MS Lista de Espera está caído o falla mucho
    public Optional<Paciente> fallbackObtenerPaciente(String especialidad, Throwable t) {
        log.error("CIRCUIT BREAKER ACTIVADO - Falló la comunicación con Lista de Espera: {}", t.getMessage());
        return Optional.empty(); // Retornamos vacío para que el flujo de reasignación no explote
    }
}