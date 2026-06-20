package cl.rednorte.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.util.List;

class RateLimitConfigTest {

    private KeyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new RateLimitConfig().userKeyResolver();
    }

    private MockServerWebExchange exchangeConIp(String ip) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/lista-espera/atenciones")
                        .remoteAddress(new InetSocketAddress(ip, 12345)));
    }

    @Test
    @DisplayName("Con usuario autenticado por JWT, la clave de rate limiting es user:<sub>")
    void usuarioAutenticadoUsaSubDelJwt() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("usuario-123")
                .build();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, List.of());

        StepVerifier.create(resolver.resolve(exchangeConIp("10.0.0.1"))
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .expectNext("user:usuario-123")
                .verifyComplete();
    }

    @Test
    @DisplayName("Sin autenticación, la clave de rate limiting es ip:<dirección>")
    void sinAutenticacionUsaLaIpDelCliente() {
        StepVerifier.create(resolver.resolve(exchangeConIp("192.168.1.50")))
                .expectNext("ip:192.168.1.50")
                .verifyComplete();
    }

    @Test
    @DisplayName("Con autenticación que no es JWT, cae al fallback por IP")
    void autenticacionNoJwtUsaLaIpDelCliente() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("usuario", "clave", List.of());

        StepVerifier.create(resolver.resolve(exchangeConIp("172.16.0.9"))
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .expectNext("ip:172.16.0.9")
                .verifyComplete();
    }
}
