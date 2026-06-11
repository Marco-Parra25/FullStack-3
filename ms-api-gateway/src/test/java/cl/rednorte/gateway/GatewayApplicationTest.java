package cl.rednorte.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GatewayApplicationTest {

    @Autowired
    private RouteLocator routeLocator;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    @DisplayName("El contexto de Spring arranca correctamente")
    void contextLoads() {
        assertThat(routeLocator).isNotNull();
    }

    @Test
    @DisplayName("Las rutas hacia los tres microservicios están registradas en el gateway")
    void rutasDeMicroserviciosRegistradas() {
        StepVerifier.create(routeLocator.getRoutes().map(route -> route.getId()).collectList())
                .assertNext(ids -> assertThat(ids)
                        .contains("ms-lista-espera", "ms-reasignacion", "ms-notificaciones"))
                .verifyComplete();
    }
}
