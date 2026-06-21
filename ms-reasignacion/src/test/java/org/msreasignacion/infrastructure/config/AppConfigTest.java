package org.msreasignacion.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void restTemplate_DebeConstruirClienteHttp() {
        RestTemplate restTemplate = new AppConfig().restTemplate(new RestTemplateBuilder());

        assertNotNull(restTemplate);
    }
}
