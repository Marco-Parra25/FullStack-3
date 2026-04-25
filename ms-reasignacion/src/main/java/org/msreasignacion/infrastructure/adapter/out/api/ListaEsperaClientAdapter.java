package org.msreasignacion.infrastructure.adapter.out.api;

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
    private final String msListaEsperaUrl = "http://localhost:8081/api/listaespera";

    public ListaEsperaClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Paciente> obtenerSiguientePaciente(String especialidad) {
        try {
            String url = msListaEsperaUrl + "/siguiente?especialidad=" + especialidad;
            Paciente paciente = restTemplate.getForObject(url, Paciente.class);
            return Optional.ofNullable(paciente);
        } catch (Exception e) {
            log.error("Error conectando con MS Lista de Espera", e);
            return Optional.empty();
        }
    }
}