package com.mpazneb.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "actividad_id", nullable = false)
    private Integer actividadId;

    @Column(name = "actividad_titulo", nullable = false)
    private String actividadTitulo;

    @Column(name = "estudiante_alias", nullable = false)
    private String estudianteAlias;

    @Column(
            name = "codigo_temporal",
            nullable = false,
            unique = true,
            length = 10
    )
    private String codigoTemporal;

    @Column(name = "asistente_alias", nullable = false)
    private String asistenteAlias;

    @Column(nullable = false)
    private Integer curso;

    @Column(nullable = false)
    private String estado = "Pendiente";

    @Column(name = "fecha_asignacion", nullable = false)
    private Long fechaAsignacion = System.currentTimeMillis();

    public Asignacion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActividadId() {
        return actividadId;
    }

    public void setActividadId(Integer actividadId) {
        this.actividadId = actividadId;
    }

    public String getActividadTitulo() {
        return actividadTitulo;
    }

    public void setActividadTitulo(String actividadTitulo) {
        this.actividadTitulo = actividadTitulo;
    }

    public String getEstudianteAlias() {
        return estudianteAlias;
    }

    public void setEstudianteAlias(String estudianteAlias) {
        this.estudianteAlias = estudianteAlias;
    }

    public String getCodigoTemporal() {
        return codigoTemporal;
    }

    public void setCodigoTemporal(String codigoTemporal) {
        this.codigoTemporal = codigoTemporal;
    }

    public String getAsistenteAlias() {
        return asistenteAlias;
    }

    public void setAsistenteAlias(String asistenteAlias) {
        this.asistenteAlias = asistenteAlias;
    }

    public Integer getCurso() {
        return curso;
    }

    public void setCurso(Integer curso) {
        this.curso = curso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Long fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}