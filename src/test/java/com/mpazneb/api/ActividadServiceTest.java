package com.mpazneb.api;

import com.mpazneb.api.model.Actividad;

import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;

import com.mpazneb.api.service.ActividadService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActividadServiceTest {

    private ActividadRepository actividades;
    private AsignacionRepository asignaciones;
    private PreguntaRepository preguntas;
    private RespuestaRepository respuestas;

    private ActividadService servicio;

    @BeforeEach
    void preparar() {

        actividades = mock(ActividadRepository.class);
        asignaciones = mock(AsignacionRepository.class);
        preguntas = mock(PreguntaRepository.class);
        respuestas = mock(RespuestaRepository.class);

        servicio = new ActividadService(
                actividades,
                asignaciones,
                preguntas,
                respuestas
        );
    }

    private Actividad actividadValida() {

        Actividad actividad = new Actividad();

        actividad.setTitulo("Sumas basicas");
        actividad.setDescripcion("Actividad de matematicas");
        actividad.setObjetivo("Resolver sumas simples");
        actividad.setInstrucciones("Selecciona la alternativa correcta");
        actividad.setModulo("Matematica");
        actividad.setDificultad("Facil");
        actividad.setCurso(1);
        actividad.setActiva(false);

        return actividad;
    }

    private void simularGuardado() {

        when(actividades.save(any(Actividad.class)))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void crearActividadValida() {

        simularGuardado();

        Actividad actividad = actividadValida();

        Actividad resultado = servicio.guardar(actividad);

        assertNotNull(resultado);
        assertEquals("Sumas basicas", resultado.getTitulo());
        assertFalse(resultado.getActiva());

        verify(actividades).save(actividad);
    }

    @Test
    void nuevaActividadSiempreComienzaInactiva() {

        simularGuardado();

        Actividad actividad = actividadValida();
        actividad.setActiva(true);

        Actividad resultado = servicio.guardar(actividad);

        assertFalse(resultado.getActiva());

        verify(actividades).save(actividad);
    }

    @Test
    void estadoNuloSeNormalizaComoInactivo() {

        simularGuardado();

        Actividad actividad = actividadValida();
        actividad.setActiva(null);

        Actividad resultado = servicio.guardar(actividad);

        assertFalse(resultado.getActiva());
    }

    @Test
    void actualizarActividadExistente() {

        simularGuardado();

        Actividad existente = actividadValida();
        existente.setId(10);

        when(actividades.findById(10))
                .thenReturn(Optional.of(existente));

        Actividad modificada = actividadValida();
        modificada.setId(10);
        modificada.setTitulo("Nuevas sumas basicas");

        Actividad resultado = servicio.guardar(modificada);

        assertEquals(
                "Nuevas sumas basicas",
                resultado.getTitulo()
        );

        verify(actividades).save(modificada);
    }

    @Test
    void actualizarActividadInexistente() {

        Actividad actividad = actividadValida();
        actividad.setId(999);

        when(actividades.findById(999))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(actividad)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verify(actividades, never()).save(any(Actividad.class));
    }

    @Test
    void activarActividadConPreguntas() {

        simularGuardado();

        Actividad existente = actividadValida();
        existente.setId(10);

        when(actividades.findById(10))
                .thenReturn(Optional.of(existente));

        when(preguntas.existsByActividadId(10))
                .thenReturn(true);

        Actividad modificada = actividadValida();
        modificada.setId(10);
        modificada.setActiva(true);

        Actividad resultado = servicio.guardar(modificada);

        assertTrue(resultado.getActiva());

        verify(actividades).save(modificada);
    }

    @Test
    void actividadSinPreguntasNoPuedeActivarse() {

        Actividad existente = actividadValida();
        existente.setId(10);

        when(actividades.findById(10))
                .thenReturn(Optional.of(existente));

        when(preguntas.existsByActividadId(10))
                .thenReturn(false);

        Actividad modificada = actividadValida();
        modificada.setId(10);
        modificada.setActiva(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(modificada)
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(actividades, never()).save(any(Actividad.class));
    }

    @Test
    void tituloVacioBloqueaGuardado() {

        Actividad actividad = actividadValida();
        actividad.setTitulo(" ");

        verificarDatosInvalidos(actividad);
    }

    @Test
    void descripcionVaciaBloqueaGuardado() {

        Actividad actividad = actividadValida();
        actividad.setDescripcion("");

        verificarDatosInvalidos(actividad);
    }

    @Test
    void objetivoVacioBloqueaGuardado() {

        Actividad actividad = actividadValida();
        actividad.setObjetivo("");

        verificarDatosInvalidos(actividad);
    }

    @Test
    void instruccionesVaciasBloqueanGuardado() {

        Actividad actividad = actividadValida();
        actividad.setInstrucciones("");

        verificarDatosInvalidos(actividad);
    }

    @Test
    void cursoInferiorAlPermitido() {

        Actividad actividad = actividadValida();
        actividad.setCurso(0);

        verificarDatosInvalidos(actividad);
    }

    @Test
    void cursoSuperiorAlPermitido() {

        Actividad actividad = actividadValida();
        actividad.setCurso(5);

        verificarDatosInvalidos(actividad);
    }

    @Test
    void cursoNuloBloqueaGuardado() {

        Actividad actividad = actividadValida();
        actividad.setCurso(null);

        verificarDatosInvalidos(actividad);
    }

    @Test
    void moduloVacioBloqueaGuardado() {

        Actividad actividad = actividadValida();
        actividad.setModulo("");

        verificarDatosInvalidos(actividad);
    }

    @Test
    void dificultadVaciaBloqueaGuardado() {

        Actividad actividad = actividadValida();
        actividad.setDificultad("");

        verificarDatosInvalidos(actividad);
    }

    private void verificarDatosInvalidos(
            Actividad actividad
    ) {

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.guardar(actividad)
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                error.getStatusCode()
        );

        verify(actividades, never())
                .save(any(Actividad.class));
    }

    @Test
    void normalizarCamposAntesDeGuardar() {

        simularGuardado();

        Actividad actividad = actividadValida();

        actividad.setTitulo("  Sumas basicas  ");
        actividad.setDescripcion("  Descripcion  ");
        actividad.setObjetivo("  Objetivo  ");
        actividad.setInstrucciones("  Instrucciones  ");

        Actividad resultado = servicio.guardar(actividad);

        assertEquals("Sumas basicas", resultado.getTitulo());
        assertEquals("Descripcion", resultado.getDescripcion());
        assertEquals("Objetivo", resultado.getObjetivo());
        assertEquals("Instrucciones", resultado.getInstrucciones());
    }

    @Test
    void eliminarActividadSinRegistrosAsociados() {

        Actividad actividad = actividadValida();
        actividad.setId(10);

        when(actividades.findById(10))
                .thenReturn(Optional.of(actividad));

        servicio.eliminar(10);

        verify(actividades).delete(actividad);
    }

    @Test
    void eliminarActividadInexistente() {

        when(actividades.findById(999))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.eliminar(999)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verify(actividades, never()).delete(any(Actividad.class));
    }

    @Test
    void actividadActivaNoPuedeEliminarse() {

        Actividad actividad = actividadValida();
        actividad.setId(10);
        actividad.setActiva(true);

        when(actividades.findById(10))
                .thenReturn(Optional.of(actividad));

        verificarEliminacionBloqueada();
    }

    @Test
    void actividadAsignadaNoPuedeEliminarse() {

        simularActividadParaEliminar();

        when(asignaciones.existsByActividadId(10))
                .thenReturn(true);

        verificarEliminacionBloqueada();
    }

    @Test
    void actividadConRespuestasNoPuedeEliminarse() {

        simularActividadParaEliminar();

        when(respuestas.existsByActividadId(10))
                .thenReturn(true);

        verificarEliminacionBloqueada();
    }

    @Test
    void actividadConPreguntasNoPuedeEliminarse() {

        simularActividadParaEliminar();

        when(preguntas.existsByActividadId(10))
                .thenReturn(true);

        verificarEliminacionBloqueada();
    }

    private void simularActividadParaEliminar() {

        Actividad actividad = actividadValida();
        actividad.setId(10);

        when(actividades.findById(10))
                .thenReturn(Optional.of(actividad));
    }

    private void verificarEliminacionBloqueada() {

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.eliminar(10)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(actividades, never())
                .delete(any(Actividad.class));
    }

    @Test
    void consultarActividades() {

        Actividad actividad = actividadValida();
        actividad.setId(10);

        when(actividades.findAll())
                .thenReturn(List.of(actividad));

        when(actividades.findById(10))
                .thenReturn(Optional.of(actividad));

        assertEquals(1, servicio.listarTodas().size());

        assertTrue(servicio.buscarPorId(10).isPresent());
    }

    @Test
    void consultarActividadInexistente() {

        when(actividades.findById(999))
                .thenReturn(Optional.empty());

        assertTrue(servicio.buscarPorId(999).isEmpty());
    }
}
