package com.mpazneb.api.service;

import com.mpazneb.api.model.Actividad;
import com.mpazneb.api.model.Pregunta;

import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final ActividadRepository actividadRepository;
    private final AsignacionRepository asignacionRepository;
    private final RespuestaRepository respuestaRepository;

    public PreguntaService(
            PreguntaRepository preguntaRepository,
            ActividadRepository actividadRepository,
            AsignacionRepository asignacionRepository,
            RespuestaRepository respuestaRepository
    ) {
        this.preguntaRepository = preguntaRepository;
        this.actividadRepository = actividadRepository;
        this.asignacionRepository = asignacionRepository;
        this.respuestaRepository = respuestaRepository;
    }

    // 1. CONSULTAS EXISTENTES

    public List<Pregunta> listarTodas() {
        return preguntaRepository.findAll();
    }

    public Optional<Pregunta> buscarPorId(Integer id) {
        return preguntaRepository.findById(id);
    }

    public List<Pregunta> listarPorActividad(Integer actividadId) {
        return preguntaRepository.findByActividadId(actividadId);
    }

    // 2. CREAR Y ACTUALIZAR PREGUNTAS

    @Transactional
    public Pregunta guardar(Pregunta pregunta) {

        if (pregunta == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La pregunta es obligatoria."
            );
        }

        if (pregunta.getActividadId() == null
                || pregunta.getActividadId() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una actividad valida."
            );
        }

        if (pregunta.getId() != null) {

            Pregunta existente = preguntaRepository
                    .findById(pregunta.getId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Pregunta no encontrada."
                            )
                    );

            if (!Objects.equals(
                    existente.getActividadId(),
                    pregunta.getActividadId()
            )) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede trasladar una pregunta a otra actividad."
                );
            }
        }

        validarActividadEditable(pregunta.getActividadId());

        validarPregunta(pregunta);

        return preguntaRepository.save(pregunta);
    }

    // 3. ELIMINAR PREGUNTAS

    @Transactional
    public void eliminar(Integer id) {

        Pregunta pregunta = preguntaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pregunta no encontrada."
                        )
                );

        validarActividadEditable(pregunta.getActividadId());

        preguntaRepository.delete(pregunta);
    }

    // 4. PROTEGER ACTIVIDADES Y RESULTADOS

    private void validarActividadEditable(
            Integer actividadId
    ) {

        Actividad actividad = actividadRepository
                .findById(actividadId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Actividad no encontrada."
                        )
                );

        if (Boolean.TRUE.equals(actividad.getActiva())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pueden modificar preguntas de una actividad activa."
            );
        }

        if (asignacionRepository.existsByActividadId(actividadId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La actividad tiene asignaciones registradas."
            );
        }

        if (respuestaRepository.existsByActividadId(actividadId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La actividad tiene respuestas registradas."
            );
        }
    }

    // 5. VALIDACIONES PEDAGOGICAS

    private void validarPregunta(Pregunta pregunta) {

        if (esVacio(pregunta.getEnunciado())
                || esVacio(pregunta.getAlternativaA())
                || esVacio(pregunta.getAlternativaB())
                || esVacio(pregunta.getAlternativaC())
                || esVacio(pregunta.getAlternativaD())
                || esVacio(pregunta.getRespuestaCorrecta())
                || esVacio(pregunta.getRetroalimentacion())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completa todos los campos obligatorios."
            );
        }

        pregunta.setEnunciado(
                pregunta.getEnunciado().trim()
        );

        pregunta.setAlternativaA(
                pregunta.getAlternativaA().trim()
        );

        pregunta.setAlternativaB(
                pregunta.getAlternativaB().trim()
        );

        pregunta.setAlternativaC(
                pregunta.getAlternativaC().trim()
        );

        pregunta.setAlternativaD(
                pregunta.getAlternativaD().trim()
        );

        pregunta.setRetroalimentacion(
                pregunta.getRetroalimentacion().trim()
        );

        Set<String> alternativas = new HashSet<>();

        alternativas.add(
                pregunta.getAlternativaA().toLowerCase(Locale.ROOT)
        );

        alternativas.add(
                pregunta.getAlternativaB().toLowerCase(Locale.ROOT)
        );

        alternativas.add(
                pregunta.getAlternativaC().toLowerCase(Locale.ROOT)
        );

        alternativas.add(
                pregunta.getAlternativaD().toLowerCase(Locale.ROOT)
        );

        if (alternativas.size() != 4) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las cuatro alternativas deben ser diferentes."
            );
        }

        String respuestaCorrecta = pregunta
                .getRespuestaCorrecta()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!Set.of("A", "B", "C", "D")
                .contains(respuestaCorrecta)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La respuesta correcta debe ser A, B, C o D."
            );
        }

        pregunta.setRespuestaCorrecta(respuestaCorrecta);
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
