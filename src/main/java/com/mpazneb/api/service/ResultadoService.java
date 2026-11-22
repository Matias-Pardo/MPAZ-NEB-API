
package com.mpazneb.api.service;

import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.model.Respuesta;
import com.mpazneb.api.model.Resultado;

import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;
import com.mpazneb.api.repository.ResultadoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ResultadoService {

    private final ResultadoRepository resultadoRepository;
    private final AsignacionRepository asignacionRepository;
    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;

    public ResultadoService(
            ResultadoRepository resultadoRepository,
            AsignacionRepository asignacionRepository,
            PreguntaRepository preguntaRepository,
            RespuestaRepository respuestaRepository
    ) {
        this.resultadoRepository = resultadoRepository;
        this.asignacionRepository = asignacionRepository;
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
    }

    public List<Resultado> consultarPorAsignacion(
            Integer asignacionId
    ) {

        if (!asignacionRepository.existsById(asignacionId)) {
            throw new IllegalArgumentException(
                    "La asignacion no existe"
            );
        }

        return resultadoRepository.findAllByAsignacionId(
                asignacionId
        );
    }

    public Optional<Resultado> buscarPorAsignacion(
            Integer asignacionId
    ) {

        if (!asignacionRepository.existsById(asignacionId)) {
            throw new IllegalArgumentException(
                    "La asignacion no existe"
            );
        }

        return resultadoRepository.findByAsignacionId(
                asignacionId
        );
    }

    // CALCULAR RESULTADOS DEL ESTUDIANTE

    @Transactional
    public Resultado calcularYGuardar(
            Integer asignacionId
    ) {

        Asignacion asignacion =
                asignacionRepository.findById(asignacionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La asignacion no existe"
                                )
                        );

        List<Pregunta> preguntas =
                preguntaRepository.findByActividadId(
                        asignacion.getActividadId()
                );

        List<Respuesta> respuestas =
                respuestaRepository
                        .findByAsignacionIdOrderByFechaRespuestaAscIdAsc(
                                asignacionId
                        );

        int aciertos = 0;

        for (Pregunta pregunta : preguntas) {

            boolean acertada = respuestas.stream()
                    .anyMatch(respuesta ->

                            respuesta.getActividadId()
                                    .equals(asignacion.getActividadId())

                                    && respuesta.getPreguntaId()
                                    .equals(pregunta.getId())

                                    && Boolean.TRUE.equals(
                                    respuesta.getEsCorrecta()
                            )
                    );

            if (acertada) {
                aciertos++;
            }
        }

        int intentos = (int) respuestas.stream()
                .filter(respuesta ->
                        respuesta.getActividadId()
                                .equals(asignacion.getActividadId())
                )
                .count();

        Resultado resultado =
                resultadoRepository.findByAsignacionId(
                        asignacionId
                ).orElseGet(Resultado::new);

        resultado.setAsignacionId(asignacionId);
        resultado.setAciertos(aciertos);
        resultado.setTotalPreguntas(preguntas.size());
        resultado.setIntentos(intentos);

        if (resultado.getObservacion() == null) {
            resultado.setObservacion("");
        }

        if (resultado.getFechaRegistro() == null) {

            resultado.setFechaRegistro(
                    System.currentTimeMillis()
            );
        }

        // GUARDAR RESULTADO DEL ESTUDIANTE

        Resultado resultadoGuardado =
                resultadoRepository.save(resultado);

        // COMPROBAR QUE TODAS LAS PREGUNTAS
        // FUERON RESUELTAS O AGOTARON SUS TRES INTENTOS

        boolean actividadCompletada =
                !preguntas.isEmpty() &&
                        preguntas.stream().allMatch(pregunta -> {

                            List<Respuesta> respuestasPregunta =
                                    respuestas.stream()
                                            .filter(respuesta ->
                                                    asignacion.getActividadId()
                                                            .equals(respuesta.getActividadId())

                                                            && pregunta.getId()
                                                            .equals(respuesta.getPreguntaId())
                                            )
                                            .toList();

                            boolean respuestaCorrecta =
                                    respuestasPregunta.stream()
                                            .anyMatch(respuesta ->
                                                    Boolean.TRUE.equals(
                                                            respuesta.getEsCorrecta()
                                                    )
                                            );

                            return respuestaCorrecta ||
                                    respuestasPregunta.size() >= 3;
                        });

        // ACTUALIZAR ESTADO DE LA ASIGNACION

        if (actividadCompletada) {

            asignacion.setEstado("Completada");

            asignacionRepository.save(asignacion);
        }

        return resultadoGuardado;
    }

    // MODULO ASISTENTE
    // ACTUALIZAR OBSERVACION PEDAGOGICA

    @Transactional
    public Optional<Resultado> actualizarObservacion(
            Integer id,
            String observacion
    ) {

        if (
                observacion == null ||
                        observacion.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "La observacion no puede estar vacia"
            );
        }

        String texto = observacion.trim();

        if (texto.length() > 250) {

            throw new IllegalArgumentException(
                    "La observacion no puede superar 250 caracteres"
            );
        }

        return resultadoRepository.findById(id)
                .map(resultado -> {

                    resultado.setObservacion(texto);

                    return resultadoRepository.save(
                            resultado
                    );
                });
    }
}
