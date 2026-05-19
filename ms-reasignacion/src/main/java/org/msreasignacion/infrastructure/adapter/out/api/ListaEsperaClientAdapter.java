package org.msreasignacion.infrastructure.adapter.out.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.msreasignacion.domain.model.Paciente;
import org.msreasignacion.domain.port.out.PacientePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class ListaEsperaClientAdapter implements PacientePort {

    private static final Logger log = LoggerFactory.getLogger(ListaEsperaClientAdapter.class);
    private final RestTemplate restTemplate;
    private final String listaEsperaBaseUrl;
    private final String internalToken;

    public ListaEsperaClientAdapter(
            RestTemplate restTemplate,
            @Value("${rednorte.services.lista-espera.base-url}") String listaEsperaBaseUrl,
            @Value("${rednorte.services.lista-espera.internal-token}") String internalToken
    ) {
        this.restTemplate = restTemplate;
        this.listaEsperaBaseUrl = listaEsperaBaseUrl;
        this.internalToken = internalToken;
    }

    @Override
    @CircuitBreaker(name = "listaEsperaCB", fallbackMethod = "fallbackObtenerPaciente")
    public Optional<Paciente> obtenerSiguientePaciente(String especialidad) {
        String url = UriComponentsBuilder
                .fromHttpUrl(listaEsperaBaseUrl)
                .path("/api/v1/waitlist/asignaciones/siguiente")
                .queryParam("especialidad", especialidad)
                .toUriString();

        log.info("Llamando al MS Lista de Espera para especialidad: {}", especialidad);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Token", internalToken);

        try {
            ResponseEntity<Paciente> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    Paciente.class
            );

            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Lista de espera no tiene pacientes disponibles para {}", especialidad);
            return Optional.empty();
        }
    }

    public Optional<Paciente> fallbackObtenerPaciente(String especialidad, Throwable t) {
        log.error("CIRCUIT BREAKER ACTIVADO - Fallo la comunicacion con Lista de Espera: {}", t.getMessage());
        return Optional.empty();
    }
}
