package cl.rednorte.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/lista-espera")
    public Mono<Map<String, Object>> listaEsperaFallback() {
        return createFallbackResponse("ms-lista-espera", "Servicio de lista de espera temporalmente no disponible");
    }

    @GetMapping("/reasignacion")
    public Mono<Map<String, Object>> reasignacionFallback() {
        return createFallbackResponse("ms-reasignacion", "Servicio de reasignación temporalmente no disponible");
    }

    @GetMapping("/notificaciones")
    public Mono<Map<String, Object>> notificacionesFallback() {
        return createFallbackResponse("ms-notificaciones", "Servicio de notificaciones temporalmente no disponible");
    }

    @GetMapping("/bff-portal")
    public Mono<Map<String, Object>> bffPortalFallback() {
        return createFallbackResponse("bff-portal", "Portal de pacientes temporalmente no disponible");
    }

    @GetMapping("/bff-admin")
    public Mono<Map<String, Object>> bffAdminFallback() {
        return createFallbackResponse("bff-admin", "Panel administrativo temporalmente no disponible");
    }

    private Mono<Map<String, Object>> createFallbackResponse(String service, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("service", service);
        response.put("message", message);
        response.put("retryAfter", "30s");
        
        return Mono.just(response);
    }
}
