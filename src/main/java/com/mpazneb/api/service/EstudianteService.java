package com.mpazneb.api.service;

import com.mpazneb.api.model.Estudiante;
import com.mpazneb.api.repository.EstudianteRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public EstudianteService(
            EstudianteRepository estudianteRepository
    ) {
        this.estudianteRepository = estudianteRepository;
    }

    // 1. CONSULTAR ESTUDIANTES

    public List<Estudiante> listarTodos() {

        return estudianteRepository.findAll();
    }

    public List<Estudiante> listarActivos() {

        return estudianteRepository.findByActivoTrue();
    }

    public Estudiante buscarPorId(Integer id) {

        return estudianteRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Estudiante no encontrado."
                        )
                );
    }

    // 2. REGISTRAR ESTUDIANTE FICTICIO

    @Transactional
    public Estudiante registrar(String alias) {

        if (alias == null || alias.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El alias es obligatorio."
            );
        }

        String aliasNormalizado = alias
                .trim()
                .replaceAll("\\s+", " ");

        if (aliasNormalizado.length() < 2
                || aliasNormalizado.length() > 60) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El alias debe tener entre 2 y 60 caracteres."
            );
        }

        if (estudianteRepository.findByAlias(
                aliasNormalizado
        ).isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alias ya esta registrado."
            );
        }

        Estudiante estudiante = new Estudiante();

        estudiante.setAlias(aliasNormalizado);
        estudiante.setActivo(true);

        try {

            return estudianteRepository.saveAndFlush(estudiante);

        } catch (DataIntegrityViolationException error) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo registrar el alias.",
                    error
            );
        }
    }
}
