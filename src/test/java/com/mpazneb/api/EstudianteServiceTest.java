package com.mpazneb.api;

import com.mpazneb.api.model.Estudiante;
import com.mpazneb.api.repository.EstudianteRepository;
import com.mpazneb.api.service.EstudianteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class EstudianteServiceTest {

    private EstudianteRepository repository;
    private EstudianteService service;

    @BeforeEach
    void configurar() {
        repository = mock(EstudianteRepository.class);
        service = new EstudianteService(repository);
    }

    private Estudiante crearEstudiante() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1);
        estudiante.setAlias("Estudiante Demo");
        estudiante.setActivo(true);
        return estudiante;
    }

    @Test
    void listarTodosCorrectamente() {
        Estudiante estudiante = crearEstudiante();

        when(repository.findAll())
                .thenReturn(List.of(estudiante));

        assertEquals(List.of(estudiante),
                service.listarTodos());

        verify(repository).findAll();
    }

    @Test
    void listarTodosSinRegistros() {
        when(repository.findAll())
                .thenReturn(List.of());

        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    void listarEstudiantesActivos() {
        Estudiante estudiante = crearEstudiante();

        when(repository.findByActivoTrue())
                .thenReturn(List.of(estudiante));

        assertEquals(List.of(estudiante),
                service.listarActivos());

        verify(repository).findByActivoTrue();
    }

    @Test
    void listarActivosSinRegistros() {
        when(repository.findByActivoTrue())
                .thenReturn(List.of());

        assertTrue(service.listarActivos().isEmpty());
    }

    @Test
    void buscarEstudianteExistente() {
        Estudiante estudiante = crearEstudiante();

        when(repository.findById(1))
                .thenReturn(Optional.of(estudiante));

        assertEquals(estudiante,
                service.buscarPorId(1));
    }

    @Test
    void buscarEstudianteInexistente() {
        when(repository.findById(999))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.buscarPorId(999)
        );

        assertEquals(HttpStatus.NOT_FOUND,
                error.getStatusCode());
    }

    @Test
    void registrarEstudianteCorrectamente() {
        when(repository.findByAlias("Estudiante Demo"))
                .thenReturn(Optional.empty());

        when(repository.saveAndFlush(any(Estudiante.class)))
                .thenAnswer(invocacion -> {
                    Estudiante estudiante = invocacion.getArgument(0);
                    estudiante.setId(1);
                    return estudiante;
                });

        Estudiante resultado =
                service.registrar("Estudiante Demo");

        assertEquals(1, resultado.getId());
        assertEquals("Estudiante Demo", resultado.getAlias());
        assertTrue(resultado.getActivo());

        verify(repository).saveAndFlush(any(Estudiante.class));
    }

    @Test
    void normalizarAliasAntesDeRegistrar() {
        when(repository.findByAlias("Estudiante Demo"))
                .thenReturn(Optional.empty());

        when(repository.saveAndFlush(any(Estudiante.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Estudiante resultado =
                service.registrar("  Estudiante   Demo  ");

        assertEquals("Estudiante Demo", resultado.getAlias());
        assertTrue(resultado.getActivo());

        verify(repository).findByAlias("Estudiante Demo");
    }

    @Test
    void rechazarAliasNulo() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.registrar(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                error.getStatusCode());

        verifyNoInteractions(repository);
    }

    @Test
    void rechazarAliasVacio() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.registrar("   ")
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                error.getStatusCode());

        verifyNoInteractions(repository);
    }

    @Test
    void rechazarAliasDemasiadoCorto() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.registrar("A")
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                error.getStatusCode());

        verifyNoInteractions(repository);
    }

    @Test
    void rechazarAliasDemasiadoLargo() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.registrar("A".repeat(61))
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                error.getStatusCode());

        verifyNoInteractions(repository);
    }

    @Test
    void aceptarLongitudesLimite() {
        when(repository.saveAndFlush(any(Estudiante.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        assertEquals(2,
                service.registrar("AB").getAlias().length());

        assertEquals(60,
                service.registrar("A".repeat(60))
                        .getAlias().length());
    }

    @Test
    void rechazarAliasDuplicado() {
        when(repository.findByAlias("Estudiante Demo"))
                .thenReturn(Optional.of(crearEstudiante()));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.registrar("Estudiante Demo")
        );

        assertEquals(HttpStatus.CONFLICT,
                error.getStatusCode());

        verify(repository, never())
                .saveAndFlush(any(Estudiante.class));
    }

    @Test
    void controlarErrorDeIntegridad() {
        when(repository.findByAlias("Estudiante Demo"))
                .thenReturn(Optional.empty());

        when(repository.saveAndFlush(any(Estudiante.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "Alias duplicado"
                ));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.registrar("Estudiante Demo")
        );

        assertEquals(HttpStatus.CONFLICT,
                error.getStatusCode());

        assertInstanceOf(
                DataIntegrityViolationException.class,
                error.getCause()
        );
    }
}
