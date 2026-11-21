package com.mpazneb.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "resultados")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "asignacion_id", nullable = false)
    private Integer asignacionId;

    @Column(nullable = false)
    private Integer aciertos;

    @Column(name = "total_preguntas", nullable = false)
    private Integer totalPreguntas;

    @Column(nullable = false)
    private Integer intentos;

    @Column(columnDefinition = "text", nullable = false)
    private String observacion = "";

    @Column(name = "fecha_registro", nullable = false)
    private Long fechaRegistro;

    public Resultado() {
    }

    @PrePersist
    public void establecerValoresPredeterminados() {
        if (fechaRegistro == null) {
            fechaRegistro = System.currentTimeMillis();
        }
        if (observacion == null) {
            observacion = "";
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

    public Integer getAciertos() {
        return aciertos;
    }

    public void setAciertos(Integer aciertos) {
        this.aciertos = aciertos;
    }

    public Integer getTotalPreguntas() {
        return totalPreguntas;
    }

    public void setTotalPreguntas(Integer totalPreguntas) {
        this.totalPreguntas = totalPreguntas;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion == null ? "" : observacion;
    }

    public Long getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Long fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
