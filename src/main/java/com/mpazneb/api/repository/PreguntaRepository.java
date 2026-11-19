
package com.mpazneb.api.repository;

import com.mpazneb.api.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {

    List<Pregunta> findByActividadId(Integer actividadId);
}
