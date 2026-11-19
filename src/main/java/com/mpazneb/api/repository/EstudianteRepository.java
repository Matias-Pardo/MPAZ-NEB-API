package com.mpazneb.api.repository;

import com.mpazneb.api.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository
        extends JpaRepository<Estudiante, Integer> {

    Optional<Estudiante> findByAlias(String alias);

    List<Estudiante> findByActivoTrue();
}