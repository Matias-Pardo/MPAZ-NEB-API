package com.mpazneb.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ActividadRequest(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El módulo es obligatorio")
        @Pattern(
                regexp = "Lectura|Matemática",
                message = "El módulo debe ser Lectura o Matemática"
        )
        String modulo,

        @NotNull(message = "El curso es obligatorio")
        @Min(value = 1, message = "El curso mínimo es primero básico")
        @Max(value = 4, message = "El curso máximo es cuarto básico")
        Integer curso,

        @NotBlank(message = "La dificultad es obligatoria")
        @Pattern(
                regexp = "Básica|Media|Avanzada",
                message = "La dificultad debe ser Básica, Media o Avanzada"
        )
        String dificultad,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "El objetivo pedagógico es obligatorio")
        String objetivo,

        @NotBlank(message = "Las instrucciones son obligatorias")
        String instrucciones

) {
}