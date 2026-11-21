package com.mpazneb.api.controller;

import com.mpazneb.api.dto.ObservacionRequest;
import com.mpazneb.api.model.Resultado;
import com.mpazneb.api.service.ResultadoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*")
public class ResultadoController {

    private final ResultadoService resultadoService;

    public ResultadoController(
            ResultadoService resultadoService
    ) {
        this.resultadoService = resultadoService;
    }

    // CONSULTAR RESULTADOS POR ASIGNACION

    @GetMapping("/asignacion/{asignacionId}")
    public ResponseEntity<?> consultarPorAsignacion(
            @PathVariable Integer asignacionId
    ) {

        try {

            List<Resultado> resultados =
                    resultadoService.consultarPorAsignacion(
                            asignacionId
                    );

            return ResponseEntity.ok(resultados);

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error.getMessage());
        }
    }

    // CALCULAR RESULTADOS

    @PostMapping("/asignacion/{asignacionId}/calcular")
    public ResponseEntity<?> calcular(
            @PathVariable Integer asignacionId
    ) {

        try {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            resultadoService.calcularYGuardar(
                                    asignacionId
                            )
                    );

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error.getMessage());
        }
    }

    // CONSULTAR RESULTADO ACTUAL

    @GetMapping("/asignacion/{asignacionId}/actual")
    public ResponseEntity<?> consultarActual(
            @PathVariable Integer asignacionId
    ) {

        try {

            return resultadoService
                    .buscarPorAsignacion(asignacionId)
                    .map(ResponseEntity::ok)
                    .orElse(
                            ResponseEntity.notFound().build()
                    );

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error.getMessage());
        }
    }

    // MODULO ASISTENTE
    // GUARDAR OBSERVACION PEDAGOGICA

    @PatchMapping("/{id}/observacion")
    public ResponseEntity<?> actualizarObservacion(

            @PathVariable Integer id,

            @Valid @RequestBody
            ObservacionRequest solicitud
    ) {

        try {

            return resultadoService.actualizarObservacion(
                            id,
                            solicitud.getObservacion()
                    )
                    .map(ResponseEntity::ok)
                    .orElse(
                            ResponseEntity.notFound().build()
                    );

        } catch (IllegalArgumentException error) {

            return ResponseEntity
                    .badRequest()
                    .body(error.getMessage());
        }
    }
}