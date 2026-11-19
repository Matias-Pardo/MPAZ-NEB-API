package com.mpazneb.api;

import com.mpazneb.api.model.Asignacion;
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

        AsignacionRepository repositorio =
                mock(AsignacionRepository.class);

        when(repositorio.findByCodigoTemporal(anyString()))
                .thenReturn(Optional.empty());

        when(repositorio.save(any(Asignacion.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0));

        AsignacionService servicio =
                new AsignacionService(repositorio);

        Asignacion asignacion = new Asignacion();

        Asignacion resultado =
                servicio.crearAsignacion(asignacion);

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

        Asignacion asignacion = new Asignacion();
        asignacion.setCodigoTemporal("ABC1234567");

        when(repositorio.findByCodigoTemporal("ABC1234567"))
                .thenReturn(Optional.of(asignacion));

        AsignacionService servicio =
                new AsignacionService(repositorio);

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
}