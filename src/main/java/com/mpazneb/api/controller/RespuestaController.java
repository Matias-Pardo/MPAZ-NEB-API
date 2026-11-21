package com.mpazneb.api.controller;

import com.mpazneb.api.dto.RespuestaRequest;
import com.mpazneb.api.model.Respuesta;
import com.mpazneb.api.service.RespuestaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/respuestas")
@CrossOrigin(origins = "*")
public class RespuestaController {

    private final RespuestaService respuestaService;

    public RespuestaController(RespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody RespuestaRequest solicitud) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(respuestaService.registrar(solicitud));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(error.getMessage());
        } catch (IllegalStateException error) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error.getMessage());
        }
    }

    @GetMapping("/asignacion/{asignacionId}")
    public ResponseEntity<?> consultarPorAsignacion(@PathVariable Integer asignacionId) {
        try {
            List<Respuesta> respuestas = respuestaService.consultarPorAsignacion(asignacionId);
            return ResponseEntity.ok(respuestas);
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getMessage());
        }
    }
}
