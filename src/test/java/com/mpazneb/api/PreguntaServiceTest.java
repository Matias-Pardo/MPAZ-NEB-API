package com.mpazneb.api;

import com.mpazneb.api.model.Actividad;
import com.mpazneb.api.model.Pregunta;

import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;

import com.mpazneb.api.service.PreguntaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PreguntaServiceTest {

    private PreguntaRepository preguntas;
    private ActividadRepository actividades;
    private AsignacionRepository asignaciones;
    private RespuestaRepository respuestas;

    private PreguntaService servicio;

    @BeforeEach
    void preparar() {

        preguntas = mock(PreguntaRepository.class);
        actividades = mock(ActividadRepository.class);
        asignaciones = mock(AsignacionRepository.class);
        respuestas = mock(RespuestaRepository.class);

        servicio = new PreguntaService(
                preguntas,
                actividades,
                asignaciones,
                respuestas
        );
    }

    private Pregunta preguntaValida() {

        return new Pregunta(
                null,
                1,
                "Cuanto es 2 + 2?",
                "2",
                "3",
                "4",
                "5",
                "C",
                "La suma correcta es cuatro."
        );
    }

    private void simularActividad(boolean activa) {

        Actividad actividad = new Actividad();

        actividad.setActiva(activa);

        when(actividades.findById(1))
                .thenReturn(Optional.of(actividad));
    }

    @Test
    void crearPreguntaValida() {

        simularActividad(false);

        Pregunta pregunta = preguntaValida();

        when(preguntas.save(any(Pregunta.class)))
                .thenAnswer(i -> i.getArgument(0));

        Pregunta resultado = servicio.guardar(pregunta);

        assertNotNull(resultado);
        assertEquals("C", resultado.getRespuestaCorrecta());

        verify(preguntas).save(pregunta);
    }

    @Test
    void actualizarPreguntaExistente() {

        simularActividad(false);

        Pregunta existente = preguntaValida();
        existente.setId(10);

        Pregunta modificada = preguntaValida();
        modificada.setId(10);
        modificada.setEnunciado("Cuanto es dos mas dos?");

        when(preguntas.findById(10))
                .thenReturn(Optional.of(existente));

        when(preguntas.save(any(Pregunta.class)))
                .thenAnswer(i -> i.getArgument(0));

        Pregunta resultado = servicio.guardar(modificada);

        assertEquals(
                "Cuanto es dos mas dos?",
                resultado.getEnunciado()
        );

        verify(preguntas).save(modificada);
    }

    @Test
    void actividadInexistenteBloqueaGuardado() {

        when(actividades.findById(1))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(preguntaValida())
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void actividadActivaBloqueaGuardado() {

        simularActividad(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(preguntaValida())
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void actividadAsignadaBloqueaGuardado() {

        simularActividad(false);

        when(asignaciones.existsByActividadId(1))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(preguntaValida())
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void actividadConRespuestasBloqueaGuardado() {

        simularActividad(false);

        when(respuestas.existsByActividadId(1))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(preguntaValida())
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void enunciadoVacioNoPermiteGuardar() {

        simularActividad(false);

        Pregunta pregunta = preguntaValida();
        pregunta.setEnunciado(" ");

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(pregunta)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void alternativasRepetidasNoPermitenGuardar() {

        simularActividad(false);

        Pregunta pregunta = preguntaValida();
        pregunta.setAlternativaA("4");

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(pregunta)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void respuestaCorrectaInvalidaBloqueaGuardado() {

        simularActividad(false);

        Pregunta pregunta = preguntaValida();
        pregunta.setRespuestaCorrecta("Z");

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(pregunta)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void noPermiteTrasladarPregunta() {

        Pregunta existente = preguntaValida();
        existente.setId(10);

        Pregunta modificada = preguntaValida();
        modificada.setId(10);
        modificada.setActividadId(2);

        when(preguntas.findById(10))
                .thenReturn(Optional.of(existente));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(modificada)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void actualizarPreguntaInexistente() {

        Pregunta pregunta = preguntaValida();
        pregunta.setId(999);

        when(preguntas.findById(999))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(pregunta)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verify(preguntas, never()).save(any(Pregunta.class));
    }

    @Test
    void eliminarPreguntaValida() {

        simularActividad(false);

        Pregunta pregunta = preguntaValida();
        pregunta.setId(10);

        when(preguntas.findById(10))
                .thenReturn(Optional.of(pregunta));

        servicio.eliminar(10);

        verify(preguntas).delete(pregunta);
    }

    @Test
    void eliminarPreguntaDeActividadActiva() {

        simularActividad(true);

        Pregunta pregunta = preguntaValida();
        pregunta.setId(10);

        when(preguntas.findById(10))
                .thenReturn(Optional.of(pregunta));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.eliminar(10)
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(preguntas, never()).delete(any(Pregunta.class));
    }

    @Test
    void eliminarPreguntaInexistente() {

        when(preguntas.findById(999))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.eliminar(999)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verify(preguntas, never()).delete(any(Pregunta.class));
    }

    @Test
    void consultarPreguntas() {

        Pregunta pregunta = preguntaValida();
        pregunta.setId(10);

        when(preguntas.findAll())
                .thenReturn(List.of(pregunta));

        when(preguntas.findById(10))
                .thenReturn(Optional.of(pregunta));

        when(preguntas.findByActividadId(1))
                .thenReturn(List.of(pregunta));

        assertEquals(1, servicio.listarTodas().size());

        assertTrue(servicio.buscarPorId(10).isPresent());

        assertEquals(
                1,
                servicio.listarPorActividad(1).size()
        );
    }

    @Test
    void normalizarCamposAntesDeGuardar() {

        simularActividad(false);

        Pregunta pregunta = preguntaValida();

        pregunta.setEnunciado("  Cuanto es 2 + 2?  ");
        pregunta.setRespuestaCorrecta(" c ");
        pregunta.setAlternativaA(" 2 ");

        when(preguntas.save(any(Pregunta.class)))
                .thenAnswer(i -> i.getArgument(0));

        Pregunta resultado = servicio.guardar(pregunta);

        assertEquals(
                "Cuanto es 2 + 2?",
                resultado.getEnunciado()
        );

        assertEquals("C", resultado.getRespuestaCorrecta());

        assertEquals("2", resultado.getAlternativaA());
    }
}
