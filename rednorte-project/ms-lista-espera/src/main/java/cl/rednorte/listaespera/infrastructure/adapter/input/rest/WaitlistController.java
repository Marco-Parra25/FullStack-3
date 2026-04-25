package cl.rednorte.listaespera.infrastructure.adapter.input.rest;

import cl.rednorte.listaespera.domain.model.WaitlistItem;
import cl.rednorte.listaespera.domain.port.input.WaitlistUseCase;
import cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto.RegistroRequest;
import cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto.WaitlistResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/waitlist")
public class WaitlistController {

    private final WaitlistUseCase waitlistUseCase;

    public WaitlistController(WaitlistUseCase waitlistUseCase) {
        this.waitlistUseCase = waitlistUseCase;
    }

    @PostMapping
    public ResponseEntity<WaitlistResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        WaitlistItem item = waitlistUseCase.registrarPaciente(
                request.pacienteId(),
                request.tipoAtencion(),
                request.especialidad()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(WaitlistResponse.from(item));
    }

    @GetMapping
    public ResponseEntity<List<WaitlistResponse>> listarTodos() {
        return ResponseEntity.ok(
                waitlistUseCase.listarTodos().stream()
                        .map(WaitlistResponse::from)
                        .toList()
        );
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<WaitlistResponse>> listarPorEspecialidad(@PathVariable String especialidad) {
        return ResponseEntity.ok(
                waitlistUseCase.listarPorEspecialidad(especialidad).stream()
                        .map(WaitlistResponse::from)
                        .toList()
        );
    }

    @GetMapping("/prioridad")
    public ResponseEntity<List<WaitlistResponse>> listarPorPrioridad() {
        return ResponseEntity.ok(
                waitlistUseCase.listarPorPrioridad().stream()
                        .map(WaitlistResponse::from)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaitlistResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(WaitlistResponse.from(waitlistUseCase.obtenerPorId(id)));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        waitlistUseCase.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarEnEspera() {
        return ResponseEntity.ok(Map.of("enEspera", waitlistUseCase.contarEnEspera()));
    }
}
