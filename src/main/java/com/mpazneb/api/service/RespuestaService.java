package com.mpazneb.api.service;

import com.mpazneb.api.dto.RespuestaRequest;
import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.model.Respuesta;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.RespuestaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class RespuestaService {

    private static final short MAX_INTENTOS_POR_PREGUNTA = 3;

    private final RespuestaRepository respuestaRepository;
    private final AsignacionRepository asignacionRepository;
    private final ActividadRepository actividadRepository;
    private final PreguntaRepository preguntaRepository;

    public RespuestaService(
            RespuestaRepository respuestaRepository,
            AsignacionRepository asignacionRepository,
            ActividadRepository actividadRepository,
            PreguntaRepository preguntaRepository
    ) {
        this.respuestaRepository = respuestaRepository;
        this.asignacionRepository = asignacionRepository;
        this.actividadRepository = actividadRepository;
        this.preguntaRepository = preguntaRepository;
    }

    public List<Respuesta> consultarPorAsignacion(Integer asignacionId) {
        if (!asignacionRepository.existsById(asignacionId)) {
            throw new IllegalArgumentException("La asignacion no existe");
        }
        return respuestaRepository.findByAsignacionIdOrderByFechaRespuestaAscIdAsc(asignacionId);
    }

    @Transactional
    public Respuesta registrar(RespuestaRequest solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede estar vacia");
        }

        Asignacion asignacion = asignacionRepository.findById(solicitud.getAsignacionId())
                .orElseThrow(() -> new IllegalArgumentException("La asignacion no existe"));

        if (!asignacion.getActividadId().equals(solicitud.getActividadId())) {
            throw new IllegalArgumentException("La actividad no corresponde a la asignacion");
        }

        if (!actividadRepository.existsById(solicitud.getActividadId())) {
            throw new IllegalArgumentException("La actividad no existe");
        }

        Pregunta pregunta = preguntaRepository.findById(solicitud.getPreguntaId())
                .orElseThrow(() -> new IllegalArgumentException("La pregunta no existe"));

        if (!pregunta.getActividadId().equals(asignacion.getActividadId())) {
            throw new IllegalArgumentException("La pregunta no corresponde a la actividad");
        }

        String alternativa = solicitud.getRespuestaSeleccionada().trim().toUpperCase(Locale.ROOT);
        if (!alternativa.matches("[A-D]")) {
            throw new IllegalArgumentException("La respuesta seleccionada debe ser A, B, C o D");
        }

        List<Respuesta> intentos = respuestaRepository
                .findByAsignacionIdAndActividadIdAndPreguntaIdOrderByNumeroIntentoAsc(
                        asignacion.getId(),
                        asignacion.getActividadId(),
                        pregunta.getId()
                );

        boolean yaCorrecta = intentos.stream().anyMatch(Respuesta::getEsCorrecta);
        if (yaCorrecta) {
            throw new IllegalStateException("La pregunta ya fue respondida correctamente");
        }

        short siguienteIntento = (short) (intentos.size() + 1);
        if (siguienteIntento > MAX_INTENTOS_POR_PREGUNTA) {
            throw new IllegalStateException("Se alcanzaron los tres intentos permitidos");
        }

        if (!intentos.isEmpty()) {
            Respuesta ultimo = intentos.get(intentos.size() - 1);
            if (alternativa.equals(ultimo.getRespuestaSeleccionada())
                    && ultimo.getFechaRespuesta() != null
                    && System.currentTimeMillis() - ultimo.getFechaRespuesta() < 1000) {
                return ultimo;
            }
        }

        boolean esCorrecta = alternativa.equals(
                pregunta.getRespuestaCorrecta().trim().toUpperCase(Locale.ROOT)
        );

        Respuesta respuesta = new Respuesta();
        respuesta.setAsignacionId(asignacion.getId());
        respuesta.setActividadId(asignacion.getActividadId());
        respuesta.setPreguntaId(pregunta.getId());
        respuesta.setRespuestaSeleccionada(alternativa);
        respuesta.setEsCorrecta(esCorrecta);
        respuesta.setNumeroIntento(siguienteIntento);
        respuesta.setFechaRespuesta(System.currentTimeMillis());

        return respuestaRepository.save(respuesta);
    }
}
