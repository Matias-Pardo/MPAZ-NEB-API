
package com.mpazneb.api.service;

import com.mpazneb.api.model.Actividad;
import com.mpazneb.api.repository.ActividadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public ActividadService(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public List<Actividad> listarTodas() {
        return actividadRepository.findAll();
    }

    public Optional<Actividad> buscarPorId(Integer id) {
        return actividadRepository.findById(id);
    }

    public Actividad guardar(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    public void eliminar(Integer id) {
        actividadRepository.deleteById(id);
    }
}
