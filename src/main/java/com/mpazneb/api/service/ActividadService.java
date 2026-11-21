package com.mpazneb.api.service;

import com.mpazneb.api.model.Actividad;
import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final AsignacionRepository asignacionRepository;
    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;

    public ActividadService(
            ActividadRepository actividadRepository,
            AsignacionRepository asignacionRepository,
            PreguntaRepository preguntaRepository,
            RespuestaRepository respuestaRepository
    ) {
        this.actividadRepository = actividadRepository;
        this.asignacionRepository = asignacionRepository;
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
    }

    // 1. LISTAR ACTIVIDADES

    public List<Actividad> listarTodas() {
        return actividadRepository.findAll();
    }

    // 2. BUSCAR ACTIVIDAD POR ID

    public Optional<Actividad> buscarPorId(Integer id) {
        return actividadRepository.findById(id);
    }

    // 3. CREAR O ACTUALIZAR ACTIVIDAD

    @Transactional
    public Actividad guardar(Actividad actividad) {

        validarActividad(actividad);

        if (actividad.getId() == null) {

            // Toda actividad nueva comienza inactiva.
            actividad.setActiva(false);

        } else {

            actividadRepository.findById(actividad.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "La actividad no existe."
                    ));

            // No permitir actividades activas sin preguntas.
            if (Boolean.TRUE.equals(actividad.getActiva())
                    && !preguntaRepository.existsByActividadId(
                            actividad.getId()
                    )) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Agrega al menos una pregunta antes de activar la actividad."
                );
            }
        }

        return actividadRepository.save(actividad);
    }

    // 4. ELIMINAR ACTIVIDAD DE FORMA PROTEGIDA

    @Transactional
    public void eliminar(Integer id) {

        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La actividad no existe."
                ));

        if (Boolean.TRUE.equals(actividad.getActiva())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Desactiva la actividad antes de eliminarla."
            );
        }

        if (asignacionRepository.existsByActividadId(id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una actividad con asignaciones."
            );
        }

        if (respuestaRepository.existsByActividadId(id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una actividad con respuestas."
            );
        }

        if (preguntaRepository.existsByActividadId(id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una actividad con preguntas."
            );
        }

        actividadRepository.delete(actividad);
    }

    // 5. VALIDACIONES PEDAGOGICAS

    private void validarActividad(Actividad actividad) {

        if (esVacio(actividad.getTitulo())
                || esVacio(actividad.getDescripcion())
                || esVacio(actividad.getObjetivo())
                || esVacio(actividad.getInstrucciones())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completa todos los campos obligatorios."
            );
        }

        if (actividad.getCurso() == null
                || actividad.getCurso() < 1
                || actividad.getCurso() > 4) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El curso debe estar entre primero y cuarto basico."
            );
        }

        if (esVacio(actividad.getModulo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El modulo es obligatorio."
            );
        }

        if (esVacio(actividad.getDificultad())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dificultad es obligatoria."
            );
        }

        if (actividad.getActiva() == null) {
            actividad.setActiva(false);
        }

        actividad.setTitulo(actividad.getTitulo().trim());
        actividad.setDescripcion(actividad.getDescripcion().trim());
        actividad.setObjetivo(actividad.getObjetivo().trim());
        actividad.setInstrucciones(actividad.getInstrucciones().trim());
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
