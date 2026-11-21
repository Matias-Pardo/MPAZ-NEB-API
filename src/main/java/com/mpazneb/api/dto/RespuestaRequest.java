package com.mpazneb.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RespuestaRequest {

    @NotNull
    private Integer asignacionId;

    @NotNull
    private Integer actividadId;

    @NotNull
    private Integer preguntaId;

    @NotBlank
    @Pattern(regexp = "[A-D]")
    private String respuestaSeleccionada;

    public Integer getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(Integer asignacionId) {
        this.asignacionId = asignacionId;
    }

    public Integer getActividadId() {
        return actividadId;
    }

    public void setActividadId(Integer actividadId) {
        this.actividadId = actividadId;
    }

    public Integer getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(Integer preguntaId) {
        this.preguntaId = preguntaId;
    }

    public String getRespuestaSeleccionada() {
        return respuestaSeleccionada;
    }

    public void setRespuestaSeleccionada(String respuestaSeleccionada) {
        this.respuestaSeleccionada = respuestaSeleccionada;
    }
}
