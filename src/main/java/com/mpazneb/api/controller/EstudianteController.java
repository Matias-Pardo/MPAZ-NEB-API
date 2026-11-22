package com.mpazneb.api.controller;

import com.mpazneb.api.model.Estudiante;
import com.mpazneb.api.service.EstudianteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(
            EstudianteService estudianteService
    ) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<Estudiante>> listarTodos() {

        return ResponseEntity.ok(
                estudianteService.listarTodos()
        );
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Estudiante>> listarActivos() {

        return ResponseEntity.ok(
                estudianteService.listarActivos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> buscarPorId(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                estudianteService.buscarPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<Estudiante> registrar(
            @RequestBody EstudianteRequest solicitud
    ) {

        Estudiante estudiante = estudianteService.registrar(
                solicitud.alias()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(estudiante);
    }

    public record EstudianteRequest(String alias) {
    }
}
