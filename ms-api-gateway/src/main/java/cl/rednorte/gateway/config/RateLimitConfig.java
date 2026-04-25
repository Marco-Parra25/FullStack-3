package cl.rednorte.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Extraer el usuario del token JWT o usar IP como fallback
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // En un entorno real, extraeríamos el subject del JWT
                return Mono.just("user:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
            }
            return Mono.just("ip:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }
}
