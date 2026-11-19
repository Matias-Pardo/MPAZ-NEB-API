
package com.mpazneb.api.controller;

import com.mpazneb.api.model.Actividad;
import com.mpazneb.api.service.ActividadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@CrossOrigin(origins = "*")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public ResponseEntity<List<Actividad>> listarTodas() {
        return ResponseEntity.ok(actividadService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actividad> buscarPorId(@PathVariable Integer id) {
        return actividadService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Actividad> crear(@RequestBody Actividad actividad) {
        actividad.setId(null);
        return ResponseEntity.ok(actividadService.guardar(actividad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Actividad> actualizar(
            @PathVariable Integer id,
            @RequestBody Actividad actividad) {

        if (actividadService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        actividad.setId(id);
        return ResponseEntity.ok(actividadService.guardar(actividad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        if (actividadService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
