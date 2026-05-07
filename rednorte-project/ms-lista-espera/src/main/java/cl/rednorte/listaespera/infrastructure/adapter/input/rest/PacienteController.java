package cl.rednorte.listaespera.infrastructure.adapter.input.rest;

import cl.rednorte.listaespera.domain.model.Paciente;
import cl.rednorte.listaespera.domain.port.input.PacienteUseCase;
import cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto.PacienteRequest;
import cl.rednorte.listaespera.infrastructure.adapter.input.rest.dto.PacienteResponse;
import cl.rednorte.listaespera.infrastructure.exception.PacienteNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteUseCase pacienteUseCase;

    public PacienteController(PacienteUseCase pacienteUseCase) {
        this.pacienteUseCase = pacienteUseCase;
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> registrar(@Valid @RequestBody PacienteRequest request) {
        Paciente paciente = Paciente.builder()
                .rut(request.rut())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .telefono(request.telefono())
                .email(request.email())
                .build();

        Paciente registrado = pacienteUseCase.registrar(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(PacienteResponse.from(registrado));
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> listarTodos() {
        return ResponseEntity.ok(
                pacienteUseCase.listarTodos().stream()
                        .map(PacienteResponse::from)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> obtener(@PathVariable Long id) {
        Paciente paciente = pacienteUseCase.obtenerPorId(id)
                .orElseThrow(() -> new PacienteNotFoundException(id));
        return ResponseEntity.ok(PacienteResponse.from(paciente));
    }
}
