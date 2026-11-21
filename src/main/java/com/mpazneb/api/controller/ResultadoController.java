package com.mpazneb.api.controller;

import com.mpazneb.api.model.Resultado;
import com.mpazneb.api.service.ResultadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*")
public class ResultadoController {

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @GetMapping("/asignacion/{asignacionId}")
    public ResponseEntity<?> consultarPorAsignacion(@PathVariable Integer asignacionId) {
        try {
            List<Resultado> resultados = resultadoService.consultarPorAsignacion(asignacionId);
            return ResponseEntity.ok(resultados);
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getMessage());
        }
    }

    @PostMapping("/asignacion/{asignacionId}/calcular")
    public ResponseEntity<?> calcular(@PathVariable Integer asignacionId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(resultadoService.calcularYGuardar(asignacionId));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getMessage());
        }
    }

    @GetMapping("/asignacion/{asignacionId}/actual")
    public ResponseEntity<?> consultarActual(@PathVariable Integer asignacionId) {
        try {
            return resultadoService.buscarPorAsignacion(asignacionId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getMessage());
        }
    }
}
