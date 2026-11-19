package com.mpazneb.api.controller;

import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.service.AsignacionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(
            AsignacionService asignacionService
    ) {
        this.asignacionService = asignacionService;
    }

    // Consultar todas las asignaciones
    @GetMapping
    public ResponseEntity<List<Asignacion>> listarTodas() {
        return ResponseEntity.ok(
                asignacionService.listarTodas()
        );
    }

    // Buscar asignación por ID
    @GetMapping("/{id}")
    public ResponseEntity<Asignacion> buscarPorId(
            @PathVariable Integer id
    ) {
        return asignacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar asignación por código
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Asignacion> buscarPorCodigo(
            @PathVariable String codigo
    ) {
        return asignacionService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Consultar asignaciones de un estudiante
    @GetMapping("/estudiante/{alias}")
    public ResponseEntity<List<Asignacion>> buscarPorEstudiante(
            @PathVariable String alias
    ) {
        return ResponseEntity.ok(
                asignacionService.buscarPorEstudiante(alias)
        );
    }

    // Crear una asignación
    @PostMapping
    public ResponseEntity<Asignacion> crear(
            @RequestBody Asignacion asignacion
    ) {
        return ResponseEntity.ok(
                asignacionService.crearAsignacion(asignacion)
        );
    }
}