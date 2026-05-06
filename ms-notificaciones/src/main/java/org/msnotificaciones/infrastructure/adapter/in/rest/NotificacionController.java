package org.msnotificaciones.infrastructure.adapter.in.rest;

import org.msnotificaciones.application.usecase.NotificarPacienteUseCase;
import org.msnotificaciones.domain.event.CupoAsignadoEvent;
import org.msnotificaciones.infrastructure.adapter.in.kafka.dto.CupoAsignadoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificarPacienteUseCase useCase;

    public NotificacionController(NotificarPacienteUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> testNotificacion(@RequestBody CupoAsignadoDTO dto) {
        // Mapeamos DTO a Evento (Igual que en el Consumer de Kafka)
        CupoAsignadoEvent evento = new CupoAsignadoEvent(
                dto.getPacienteRut(),
                dto.getEmail(),
                dto.getTelefono(),
                dto.getEspecialidad()
        );

        useCase.ejecutar(evento);
        return ResponseEntity.ok("Notificación enviada exitosamente al RUT: " + dto.getPacienteRut());
    }
}