package org.msreasignacion.infrastructure.adapter.in.web;

import org.msreasignacion.application.usecase.ReasignarCupoUseCase;
import org.msreasignacion.domain.dto.ReasignacionDTO; // Importa tu nuevo DTO
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reasignaciones")
public class ReasignacionController {

    private final ReasignarCupoUseCase useCase;

    public ReasignacionController(ReasignarCupoUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReasignacionDTO> consultar(@PathVariable UUID id) {
        return useCase.obtenerDetalle(id)
                .map(r -> new ReasignacionDTO(
                        r.getId(),
                        r.getPacienteRut(),
                        r.getEspecialidad(),
                        r.getEstado(),
                        r.getFechaAsignacion()
                )) // Transformamos el modelo de dominio a DTO aquí
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}