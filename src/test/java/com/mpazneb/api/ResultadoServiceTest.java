
package com.mpazneb.api;

import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.model.Respuesta;
import com.mpazneb.api.model.Resultado;

import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;
import com.mpazneb.api.repository.ResultadoRepository;

import com.mpazneb.api.service.ResultadoService;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResultadoServiceTest {

    // CALCULAR ACIERTOS, PREGUNTAS E INTENTOS

    @Test
    void calculaAciertosYTotalPreguntas() {

        ResultadoRepository resultados = mock(ResultadoRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        RespuestaRepository respuestas = mock(RespuestaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);

        Pregunta pregunta1 = new Pregunta();
        pregunta1.setId(30);
        pregunta1.setActividadId(20);

        Pregunta pregunta2 = new Pregunta();
        pregunta2.setId(31);
        pregunta2.setActividadId(20);

        Respuesta correcta = new Respuesta();
        correcta.setActividadId(20);
        correcta.setPreguntaId(30);
        correcta.setEsCorrecta(true);

        Respuesta incorrecta = new Respuesta();
        incorrecta.setActividadId(20);
        incorrecta.setPreguntaId(31);
        incorrecta.setEsCorrecta(false);

        when(asignaciones.findById(10))
                .thenReturn(Optional.of(asignacion));

        when(preguntas.findByActividadId(20))
                .thenReturn(List.of(pregunta1, pregunta2));

        when(respuestas.findByAsignacionIdOrderByFechaRespuestaAscIdAsc(10))
                .thenReturn(List.of(correcta, incorrecta));

        when(resultados.findByAsignacionId(10))
                .thenReturn(Optional.empty());

        when(resultados.save(any(Resultado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoService servicio = new ResultadoService(
                resultados,
                asignaciones,
                preguntas,
                respuestas
        );

        Resultado resultado = servicio.calcularYGuardar(10);

        assertEquals(1, resultado.getAciertos());
        assertEquals(2, resultado.getTotalPreguntas());
        assertEquals(2, resultado.getIntentos());
        assertEquals("", resultado.getObservacion());
        assertNotNull(resultado.getFechaRegistro());

        assertEquals("Pendiente", asignacion.getEstado());

        verify(asignaciones, never())
                .save(any(Asignacion.class));
    }

    // VALIDAR ASIGNACION INEXISTENTE

    @Test
    void rechazaAsignacionInexistente() {

        ResultadoRepository resultados = mock(ResultadoRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        RespuestaRepository respuestas = mock(RespuestaRepository.class);

        when(asignaciones.findById(999))
                .thenReturn(Optional.empty());

        ResultadoService servicio = new ResultadoService(
                resultados,
                asignaciones,
                preguntas,
                respuestas
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.calcularYGuardar(999)
        );

        assertEquals(
                "La asignacion no existe",
                error.getMessage()
        );

        verify(resultados, never())
                .save(any(Resultado.class));
    }

    // COMPLETAR ASIGNACION CON RESPUESTAS CORRECTAS

    @Test
    void marcaAsignacionCompletadaConTodasLasRespuestasCorrectas() {

        ResultadoRepository resultados = mock(ResultadoRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        RespuestaRepository respuestas = mock(RespuestaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);
        asignacion.setEstado("Pendiente");

        Pregunta pregunta1 = new Pregunta();
        pregunta1.setId(30);
        pregunta1.setActividadId(20);

        Pregunta pregunta2 = new Pregunta();
        pregunta2.setId(31);
        pregunta2.setActividadId(20);

        Respuesta respuesta1 = new Respuesta();
        respuesta1.setActividadId(20);
        respuesta1.setPreguntaId(30);
        respuesta1.setEsCorrecta(true);

        Respuesta respuesta2 = new Respuesta();
        respuesta2.setActividadId(20);
        respuesta2.setPreguntaId(31);
        respuesta2.setEsCorrecta(true);

        when(asignaciones.findById(10))
                .thenReturn(Optional.of(asignacion));

        when(preguntas.findByActividadId(20))
                .thenReturn(List.of(pregunta1, pregunta2));

        when(respuestas.findByAsignacionIdOrderByFechaRespuestaAscIdAsc(10))
                .thenReturn(List.of(respuesta1, respuesta2));

        when(resultados.findByAsignacionId(10))
                .thenReturn(Optional.empty());

        when(resultados.save(any(Resultado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoService servicio = new ResultadoService(
                resultados,
                asignaciones,
                preguntas,
                respuestas
        );

        Resultado resultado = servicio.calcularYGuardar(10);

        assertEquals(2, resultado.getAciertos());
        assertEquals(2, resultado.getTotalPreguntas());
        assertEquals(2, resultado.getIntentos());

        assertEquals(
                "Completada",
                asignacion.getEstado()
        );

        verify(asignaciones).save(asignacion);
    }

    // COMPLETAR ASIGNACION AL AGOTAR LOS TRES INTENTOS

    @Test
    void marcaAsignacionCompletadaAlAgotarTresIntentos() {

        ResultadoRepository resultados = mock(ResultadoRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        RespuestaRepository respuestas = mock(RespuestaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);
        asignacion.setEstado("Pendiente");

        Pregunta pregunta = new Pregunta();
        pregunta.setId(30);
        pregunta.setActividadId(20);

        Respuesta intento1 = new Respuesta();
        intento1.setActividadId(20);
        intento1.setPreguntaId(30);
        intento1.setEsCorrecta(false);

        Respuesta intento2 = new Respuesta();
        intento2.setActividadId(20);
        intento2.setPreguntaId(30);
        intento2.setEsCorrecta(false);

        Respuesta intento3 = new Respuesta();
        intento3.setActividadId(20);
        intento3.setPreguntaId(30);
        intento3.setEsCorrecta(false);

        when(asignaciones.findById(10))
                .thenReturn(Optional.of(asignacion));

        when(preguntas.findByActividadId(20))
                .thenReturn(List.of(pregunta));

        when(respuestas.findByAsignacionIdOrderByFechaRespuestaAscIdAsc(10))
                .thenReturn(List.of(intento1, intento2, intento3));

        when(resultados.findByAsignacionId(10))
                .thenReturn(Optional.empty());

        when(resultados.save(any(Resultado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoService servicio = new ResultadoService(
                resultados,
                asignaciones,
                preguntas,
                respuestas
        );

        Resultado resultado = servicio.calcularYGuardar(10);

        assertEquals(0, resultado.getAciertos());
        assertEquals(1, resultado.getTotalPreguntas());
        assertEquals(3, resultado.getIntentos());

        assertEquals(
                "Completada",
                asignacion.getEstado()
        );

        verify(asignaciones).save(asignacion);
    }

    // EVITAR COMPLETAR UNA ACTIVIDAD SIN PREGUNTAS

    @Test
    void mantienePendienteUnaActividadSinPreguntas() {

        ResultadoRepository resultados = mock(ResultadoRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        RespuestaRepository respuestas = mock(RespuestaRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(10);
        asignacion.setActividadId(20);
        asignacion.setEstado("Pendiente");

        when(asignaciones.findById(10))
                .thenReturn(Optional.of(asignacion));

        when(preguntas.findByActividadId(20))
                .thenReturn(List.of());

        when(respuestas.findByAsignacionIdOrderByFechaRespuestaAscIdAsc(10))
                .thenReturn(List.of());

        when(resultados.findByAsignacionId(10))
                .thenReturn(Optional.empty());

        when(resultados.save(any(Resultado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResultadoService servicio = new ResultadoService(
                resultados,
                asignaciones,
                preguntas,
                respuestas
        );

        Resultado resultado = servicio.calcularYGuardar(10);

        assertEquals(0, resultado.getTotalPreguntas());
        assertEquals(0, resultado.getAciertos());
        assertEquals(0, resultado.getIntentos());

        assertEquals(
                "Pendiente",
                asignacion.getEstado()
        );

        verify(asignaciones, never())
                .save(any(Asignacion.class));
    }
}
