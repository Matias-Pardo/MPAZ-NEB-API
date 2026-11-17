
package com.mpazneb.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/salud")
    public Map<String, String> verificarSalud() {
        return Map.of(
                "estado", "OK",
                "mensaje", "API MPAZ NEB funcionando correctamente"
        );
    }
}
