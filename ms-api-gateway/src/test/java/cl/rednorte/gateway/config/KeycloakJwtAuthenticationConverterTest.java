package cl.rednorte.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakJwtAuthenticationConverterTest {

    private KeycloakJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakJwtAuthenticationConverter();
    }

    private Jwt jwtConClaims(Map<String, Object> claims) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("usuario-123");
        claims.forEach(builder::claim);
        return builder.build();
    }

    @Test
    @DisplayName("Convierte roles de realm_access en authorities con prefijo ROLE_ y en mayúsculas")
    void convierteRolesDeKeycloakEnAuthorities() {
        Jwt jwt = jwtConClaims(Map.of("realm_access", Map.of("roles", List.of("admin", "medico"))));

        StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> {
                    List<String> authorities = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();
                    assertThat(authorities).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_MEDICO");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("JWT sin claim realm_access produce una autenticación sin authorities")
    void jwtSinRealmAccessNoTieneAuthorities() {
        Jwt jwt = jwtConClaims(Map.of("otro_claim", "valor"));

        StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> assertThat(auth.getAuthorities()).isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("realm_access sin la clave roles produce una autenticación sin authorities")
    void realmAccessSinRolesNoTieneAuthorities() {
        Jwt jwt = jwtConClaims(Map.of("realm_access", Map.of("otra_clave", "valor")));

        StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> assertThat(auth.getAuthorities()).isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("La autenticación conserva el JWT original como principal")
    void autenticacionConservaElJwt() {
        Jwt jwt = jwtConClaims(Map.of("realm_access", Map.of("roles", List.of("paciente"))));

        StepVerifier.create(converter.convert(jwt))
                .assertNext(auth -> {
                    assertThat(auth.isAuthenticated()).isTrue();
                    assertThat(auth.getName()).isEqualTo("usuario-123");
                })
                .verifyComplete();
    }
}
