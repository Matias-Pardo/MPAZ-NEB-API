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

        when(asignaciones.findById(10)).thenReturn(Optional.of(asignacion));
        when(preguntas.findByActividadId(20)).thenReturn(List.of(pregunta1, pregunta2));
        when(respuestas.findByAsignacionIdOrderByFechaRespuestaAscIdAsc(10))
                .thenReturn(List.of(correcta, incorrecta));
        when(resultados.findByAsignacionId(10)).thenReturn(Optional.empty());
        when(resultados.save(any(Resultado.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
    }

    @Test
    void rechazaAsignacionInexistente() {
        ResultadoRepository resultados = mock(ResultadoRepository.class);
        AsignacionRepository asignaciones = mock(AsignacionRepository.class);
        PreguntaRepository preguntas = mock(PreguntaRepository.class);
        RespuestaRepository respuestas = mock(RespuestaRepository.class);

        when(asignaciones.findById(999)).thenReturn(Optional.empty());

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

        assertEquals("La asignacion no existe", error.getMessage());
        verify(resultados, never()).save(any(Resultado.class));
    }
}
