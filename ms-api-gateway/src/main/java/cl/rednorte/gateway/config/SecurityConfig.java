package cl.rednorte.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // Rutas públicas
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/fallback/**").permitAll()
                .pathMatchers("/api/lista-espera/public/**").permitAll()
                
                // Rutas de administradores y médicos
                .pathMatchers("/api/lista-espera/admin/**").hasAnyRole("ADMIN", "MEDICO")
                .pathMatchers("/api/reasignacion/**").hasAnyRole("ADMIN", "MEDICO")
                
                // Rutas de notificaciones (todos los roles autenticados)
                .pathMatchers("/api/notificaciones/**").hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                
                // Rutas de monitoreo (solo administradores)
                .pathMatchers("/api/monitoreo/**").hasRole("ADMIN")
                
                // Todas las demás rutas requieren autenticación
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
            )
            .build();
    }
}
