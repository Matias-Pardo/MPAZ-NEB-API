
package com.mpazneb.api.controller;

import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.service.PreguntaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = "*")
public class PreguntaController {

    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @GetMapping
    public ResponseEntity<List<Pregunta>> listarTodas() {
        return ResponseEntity.ok(preguntaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pregunta> buscarPorId(@PathVariable Integer id) {
        return preguntaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/actividad/{actividadId}")
    public ResponseEntity<List<Pregunta>> listarPorActividad(
            @PathVariable Integer actividadId) {

        return ResponseEntity.ok(
                preguntaService.listarPorActividad(actividadId)
        );
    }

    @PostMapping
    public ResponseEntity<Pregunta> crear(@RequestBody Pregunta pregunta) {
        pregunta.setId(null);
        return ResponseEntity.ok(preguntaService.guardar(pregunta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pregunta> actualizar(
            @PathVariable Integer id,
            @RequestBody Pregunta pregunta) {

        if (preguntaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        pregunta.setId(id);
        return ResponseEntity.ok(preguntaService.guardar(pregunta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        if (preguntaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        preguntaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

