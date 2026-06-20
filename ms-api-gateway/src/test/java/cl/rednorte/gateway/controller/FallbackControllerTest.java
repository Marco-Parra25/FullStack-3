package cl.rednorte.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FallbackControllerTest {

    private FallbackController controller;

    @BeforeEach
    void setUp() {
        controller = new FallbackController();
    }

    @Test
    @DisplayName("Fallback de lista de espera retorna 503 con el nombre del servicio")
    void listaEsperaFallbackRetorna503() {
        verificarFallback(controller.listaEsperaFallback(), "ms-lista-espera");
    }

    @Test
    @DisplayName("Fallback de reasignación retorna 503 con el nombre del servicio")
    void reasignacionFallbackRetorna503() {
        verificarFallback(controller.reasignacionFallback(), "ms-reasignacion");
    }

    @Test
    @DisplayName("Fallback de notificaciones retorna 503 con el nombre del servicio")
    void notificacionesFallbackRetorna503() {
        verificarFallback(controller.notificacionesFallback(), "ms-notificaciones");
    }

    @Test
    @DisplayName("Fallback del portal BFF retorna 503 con el nombre del servicio")
    void bffPortalFallbackRetorna503() {
        verificarFallback(controller.bffPortalFallback(), "bff-portal");
    }

    @Test
    @DisplayName("Fallback del panel admin BFF retorna 503 con el nombre del servicio")
    void bffAdminFallbackRetorna503() {
        verificarFallback(controller.bffAdminFallback(), "bff-admin");
    }

    @Test
    @DisplayName("Fallback de monitoreo retorna 503 con el nombre del servicio")
    void monitoreoFallbackRetorna503() {
        verificarFallback(controller.monitoreoFallback(), "monitoreo");
    }

    @Test
    @DisplayName("La respuesta de fallback incluye timestamp, mensaje y retryAfter")
    void respuestaIncluyeCamposEstandar() {
        StepVerifier.create(controller.listaEsperaFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body).containsKeys("timestamp", "status", "service", "message", "retryAfter");
                    assertThat(body.get("status")).isEqualTo("SERVICE_UNAVAILABLE");
                    assertThat(body.get("retryAfter")).isEqualTo("30s");
                    assertThat(body.get("message").toString()).isNotBlank();
                })
                .verifyComplete();
    }

    private void verificarFallback(Mono<ResponseEntity<Map<String, Object>>> mono, String servicioEsperado) {
        StepVerifier.create(mono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().get("service")).isEqualTo(servicioEsperado);
                })
                .verifyComplete();
    }
}
