
package com.mpazneb.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "actividades")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String modulo;
    private Integer curso;
    private String dificultad;
    private String descripcion;

    private Boolean activa = false;
    private String objetivo = "";
    private String instrucciones = "";

    public Actividad() {
    }

    public Actividad(
            Integer id,
            String titulo,
            String modulo,
            Integer curso,
            String dificultad,
            String descripcion,
            Boolean activa,
            String objetivo,
            String instrucciones
    ) {
        this.id = id;
        this.titulo = titulo;
        this.modulo = modulo;
        this.curso = curso;
        this.dificultad = dificultad;
        this.descripcion = descripcion;
        this.activa = activa != null ? activa : false;
        this.objetivo = objetivo != null ? objetivo : "";
        this.instrucciones = instrucciones != null ? instrucciones : "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Integer getCurso() {
        return curso;
    }

    public void setCurso(Integer curso) {
        this.curso = curso;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }
}
