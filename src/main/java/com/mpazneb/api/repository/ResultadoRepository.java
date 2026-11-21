package com.mpazneb.api.repository;

import com.mpazneb.api.model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultadoRepository extends JpaRepository<Resultado, Integer> {

    Optional<Resultado> findByAsignacionId(Integer asignacionId);

    List<Resultado> findAllByAsignacionId(Integer asignacionId);
}
