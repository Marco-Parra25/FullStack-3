package cl.rednorte.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    // Se mockea el decoder para no depender de un Keycloak real en las pruebas
    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    @DisplayName("Las rutas de fallback son públicas y responden 503")
    void fallbackEsPublico() {
        webTestClient.get().uri("/fallback/lista-espera")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.service").isEqualTo("ms-lista-espera")
                .jsonPath("$.status").isEqualTo("SERVICE_UNAVAILABLE");
    }

    @Test
    @DisplayName("El health check de actuator es público")
    void actuatorHealthEsPublico() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Una ruta protegida sin token responde 401 Unauthorized")
    void rutaProtegidaSinTokenRetorna401() {
        webTestClient.get().uri("/api/reasignacion/solicitudes")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST a lista de espera con rol USER responde 403 Forbidden (solo ADMIN puede)")
    void postListaEsperaConRolUsuarioRetorna403() {
        webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_USER"))
                .post().uri("/api/lista-espera/atenciones")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Reasignación con rol PACIENTE responde 403 Forbidden (requiere ADMIN o MEDICO)")
    void reasignacionConRolPacienteRetorna403() {
        webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_PACIENTE"))
                .get().uri("/api/reasignacion/solicitudes")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Reasignación con rol MEDICO pasa la seguridad (responde fallback 503, no 401/403)")
    void reasignacionConRolMedicoPasaSeguridad() {
        // El microservicio destino no está disponible en el test, por lo que el
        // circuit breaker responde con el fallback 503: la seguridad fue superada.
        webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_MEDICO"))
                .get().uri("/api/reasignacion/solicitudes")
                .exchange()
                .expectStatus().isEqualTo(503);
    }

    @Test
    @DisplayName("Notificaciones con rol PACIENTE pasa la seguridad (responde fallback 503)")
    void notificacionesConRolPacientePasaSeguridad() {
        webTestClient.mutateWith(mockJwt().authorities(() -> "ROLE_PACIENTE"))
                .get().uri("/api/notificaciones/mis-notificaciones")
                .exchange()
                .expectStatus().isEqualTo(503);
    }
}
