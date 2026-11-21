package com.mpazneb.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "respuestas")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "asignacion_id", nullable = false)
    private Integer asignacionId;

    @Column(name = "actividad_id", nullable = false)
    private Integer actividadId;

    @Column(name = "pregunta_id", nullable = false)
    private Integer preguntaId;

    @Column(name = "respuesta_seleccionada", nullable = false)
    private String respuestaSeleccionada;

    @Column(name = "es_correcta", nullable = false)
    private Boolean esCorrecta;

    @Column(name = "numero_intento", nullable = false)
    private Short numeroIntento;

    @Column(name = "fecha_respuesta", nullable = false)
    private Long fechaRespuesta;

    public Respuesta() {
    }

    @PrePersist
    public void establecerFechaRespuesta() {
        if (fechaRespuesta == null) {
            fechaRespuesta = System.currentTimeMillis();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Boolean getEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(Boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public Short getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(Short numeroIntento) {
        this.numeroIntento = numeroIntento;
    }

    public Long getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Long fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
