package com.mpazneb.api.repository;

import com.mpazneb.api.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespuestaRepository extends JpaRepository<Respuesta, Integer> {

    List<Respuesta> findByAsignacionIdOrderByFechaRespuestaAscIdAsc(Integer asignacionId);

    List<Respuesta> findByAsignacionIdAndActividadIdAndPreguntaIdOrderByNumeroIntentoAsc(
            Integer asignacionId,
            Integer actividadId,
            Integer preguntaId
    );

    Optional<Respuesta> findTopByAsignacionIdAndActividadIdAndPreguntaIdOrderByNumeroIntentoDesc(
            Integer asignacionId,
            Integer actividadId,
            Integer preguntaId
    );
}
