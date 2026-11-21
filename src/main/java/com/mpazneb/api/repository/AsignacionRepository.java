package com.mpazneb.api.repository;

import com.mpazneb.api.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository
        extends JpaRepository<Asignacion, Integer> {

    Optional<Asignacion> findByCodigoTemporal(String codigoTemporal);

    List<Asignacion> findByEstudianteAlias(String estudianteAlias);

    boolean existsByActividadId(Integer actividadId);
}