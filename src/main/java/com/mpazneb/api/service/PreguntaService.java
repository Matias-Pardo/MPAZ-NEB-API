
package com.mpazneb.api.service;

import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.repository.PreguntaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository) {
        this.preguntaRepository = preguntaRepository;
    }

    public List<Pregunta> listarTodas() {
        return preguntaRepository.findAll();
    }

    public Optional<Pregunta> buscarPorId(Integer id) {
        return preguntaRepository.findById(id);
    }

    public List<Pregunta> listarPorActividad(Integer actividadId) {
        return preguntaRepository.findByActividadId(actividadId);
    }

    public Pregunta guardar(Pregunta pregunta) {
        return preguntaRepository.save(pregunta);
    }

    public void eliminar(Integer id) {
        preguntaRepository.deleteById(id);
    }
}
