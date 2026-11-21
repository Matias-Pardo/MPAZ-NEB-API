package com.mpazneb.api;

import com.mpazneb.api.dto.RespuestaRequest;
import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.model.Respuesta;
import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;
import com.mpazneb.api.service.RespuestaService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RespuestaServiceTest {

    private RespuestaService crearServicio(
            RespuestaRepository respuestas,
            AsignacionRepository asignaciones,
            ActividadRepository actividades,
            PreguntaRepository preguntas
    ) {
        return new RespuestaService(
                respuestas,
                asignaciones,
                actividades,
                preguntas
        );
    }

    private RespuestaRequest solicitud() {
        RespuestaRequest solicitud = new RespuestaRequest();
        solicitud.setAsignacionId(10);
        solicitud.setActividadId(20);
        solicitud.setPreguntaId(30);
        solicitud.setRespuestaSeleccionada("B");
        return solicitud;
    }

    @Test
    void registraRespuestaCorrecta() {
        RespuestaRepository respuestas = mock(RespuestaRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        ActividadRepository actividades = mock(ActividadRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(30);
        pregunta.setActividadId(20);
        pregunta.setRespuestaCorrecta("B");

        when(asignaciones.findById(10)).thenReturn(Optional.of(asignacion));
        when(actividades.existsById(20)).thenReturn(true);
        when(preguntas.findById(30)).thenReturn(Optional.of(pregunta));
        when(respuestas.findByAsignacionIdAndActividadIdAndPreguntaIdOrderByNumeroIntentoAsc(
                10, 20, 30
        )).thenReturn(Collections.emptyList());
        when(respuestas.save(any(Respuesta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RespuestaService servicio = crearServicio(respuestas, asignaciones, actividades, preguntas);
        Respuesta resultado = servicio.registrar(solicitud());

        assertTrue(resultado.getEsCorrecta());
        assertEquals(Short.valueOf((short) 1), resultado.getNumeroIntento());
        assertNotNull(resultado.getFechaRespuesta());
    }

    @Test
    void rechazaAsignacionInexistente() {
        RespuestaRepository respuestas = mock(RespuestaRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        ActividadRepository actividades = mock(ActividadRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);

        when(asignaciones.findById(10)).thenReturn(Optional.empty());

        RespuestaService servicio = crearServicio(respuestas, asignaciones, actividades, preguntas);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrar(solicitud())
        );

        assertEquals("La asignacion no existe", error.getMessage());
        verify(respuestas, never()).save(any(Respuesta.class));
    }

    @Test
    void rechazaPreguntaDeOtraActividad() {
        RespuestaRepository respuestas = mock(RespuestaRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        ActividadRepository actividades = mock(ActividadRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(30);
        pregunta.setActividadId(99);

        when(asignaciones.findById(10)).thenReturn(Optional.of(asignacion));
        when(actividades.existsById(20)).thenReturn(true);
        when(preguntas.findById(30)).thenReturn(Optional.of(pregunta));

        RespuestaService servicio = crearServicio(respuestas, asignaciones, actividades, preguntas);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrar(solicitud())
        );

        assertEquals("La pregunta no corresponde a la actividad", error.getMessage());
    }

    @Test
    void bloqueaCuartoIntento() {
        RespuestaRepository respuestas = mock(RespuestaRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        ActividadRepository actividades = mock(ActividadRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(30);
        pregunta.setActividadId(20);
        pregunta.setRespuestaCorrecta("A");

        Respuesta intento1 = new Respuesta();
        intento1.setNumeroIntento((short) 1);
        intento1.setEsCorrecta(false);
        intento1.setRespuestaSeleccionada("C");
        Respuesta intento2 = new Respuesta();
        intento2.setNumeroIntento((short) 2);
        intento2.setEsCorrecta(false);
        intento2.setRespuestaSeleccionada("D");
        Respuesta intento3 = new Respuesta();
        intento3.setNumeroIntento((short) 3);
        intento3.setEsCorrecta(false);
        intento3.setRespuestaSeleccionada("B");

        when(asignaciones.findById(10)).thenReturn(Optional.of(asignacion));
        when(actividades.existsById(20)).thenReturn(true);
        when(preguntas.findById(30)).thenReturn(Optional.of(pregunta));
        when(respuestas.findByAsignacionIdAndActividadIdAndPreguntaIdOrderByNumeroIntentoAsc(
                10, 20, 30
        )).thenReturn(List.of(intento1, intento2, intento3));

        RespuestaService servicio = crearServicio(respuestas, asignaciones, actividades, preguntas);

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> servicio.registrar(solicitud())
        );

        assertEquals("Se alcanzaron los tres intentos permitidos", error.getMessage());
    }
}
