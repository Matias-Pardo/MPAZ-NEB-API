package com.mpazneb.api;

import com.mpazneb.api.model.Actividad;
import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.repository.ActividadRepository;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.service.AsignacionService;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AsignacionServiceTest {

    @Test
    void crearAsignacionGeneraCodigoYGuarda() {

        // Simular repositorios
        AsignacionRepository repositorio =
                mock(AsignacionRepository.class);

        ActividadRepository actividades =
                mock(ActividadRepository.class);

        // Simular una actividad existente y activa
        Actividad actividad = new Actividad();
        actividad.setActiva(true);

        when(actividades.findById(1))
                .thenReturn(Optional.of(actividad));

        // Simular que el código generado no existe
        when(repositorio.findByCodigoTemporal(anyString()))
                .thenReturn(Optional.empty());

        // Simular el guardado de la asignación
        when(repositorio.save(any(Asignacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Crear el servicio
        AsignacionService servicio =
                new AsignacionService(repositorio, actividades);

        // Preparar asignación
        Asignacion asignacion = new Asignacion();
        asignacion.setActividadId(1);

        // Ejecutar
        Asignacion resultado =
                servicio.crearAsignacion(asignacion);

        // Verificar resultados
        assertNotNull(resultado.getCodigoTemporal());
        assertEquals(10, resultado.getCodigoTemporal().length());
        assertEquals("Pendiente", resultado.getEstado());
        assertNotNull(resultado.getFechaAsignacion());

        verify(repositorio, times(1)).save(asignacion);
    }

    @Test
    void buscarAsignacionPorCodigo() {

        AsignacionRepository repositorio =
                mock(AsignacionRepository.class);

        ActividadRepository actividades =
                mock(ActividadRepository.class);

        Asignacion asignacion = new Asignacion();
        asignacion.setCodigoTemporal("ABC1234567");

        when(repositorio.findByCodigoTemporal("ABC1234567"))
                .thenReturn(Optional.of(asignacion));

        AsignacionService servicio =
                new AsignacionService(repositorio, actividades);

        Optional<Asignacion> resultado =
                servicio.buscarPorCodigo("ABC1234567");

        assertTrue(resultado.isPresent());

        assertEquals(
                "ABC1234567",
                resultado.get().getCodigoTemporal()
        );

        verify(repositorio, times(1))
                .findByCodigoTemporal("ABC1234567");
    }

    @Test
    void codigoInexistenteNoDevuelveAsignacion() {

        AsignacionRepository repositorio =
                mock(AsignacionRepository.class);

        ActividadRepository actividades =
                mock(ActividadRepository.class);

        when(repositorio.findByCodigoTemporal("NOEXISTE01"))
                .thenReturn(Optional.empty());

        AsignacionService servicio =
                new AsignacionService(repositorio, actividades);

        Optional<Asignacion> resultado =
                servicio.buscarPorCodigo("NOEXISTE01");

        assertTrue(resultado.isEmpty());

        verify(repositorio, times(1))
                .findByCodigoTemporal("NOEXISTE01");
    }
}