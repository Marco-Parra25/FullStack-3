package cl.rednorte.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/lista-espera")
    public Mono<ResponseEntity<Map<String, Object>>> listaEsperaFallback() {
        return createFallbackResponse("ms-lista-espera", "Servicio de lista de espera temporalmente no disponible");
    }

    @RequestMapping("/reasignacion")
    public Mono<ResponseEntity<Map<String, Object>>> reasignacionFallback() {
        return createFallbackResponse("ms-reasignacion", "Servicio de reasignación temporalmente no disponible");
    }

    @RequestMapping("/notificaciones")
    public Mono<ResponseEntity<Map<String, Object>>> notificacionesFallback() {
        return createFallbackResponse("ms-notificaciones", "Servicio de notificaciones temporalmente no disponible");
    }

    @RequestMapping("/bff-portal")
    public Mono<ResponseEntity<Map<String, Object>>> bffPortalFallback() {
        return createFallbackResponse("bff-portal", "Portal de pacientes temporalmente no disponible");
    }

    @RequestMapping("/bff-admin")
    public Mono<ResponseEntity<Map<String, Object>>> bffAdminFallback() {
        return createFallbackResponse("bff-admin", "Panel administrativo temporalmente no disponible");
    }

    @RequestMapping("/monitoreo")
    public Mono<ResponseEntity<Map<String, Object>>> monitoreoFallback() {
        return createFallbackResponse("monitoreo", "Servicio de monitoreo temporalmente no disponible");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String service, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("service", service);
        response.put("message", message);
        response.put("retryAfter", "30s");
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
