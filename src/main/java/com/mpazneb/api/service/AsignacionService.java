package com.mpazneb.api.service;

import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.repository.AsignacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Locale;
import java.util.UUID;

@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;

    public AsignacionService(
            AsignacionRepository asignacionRepository
    ) {
        this.asignacionRepository = asignacionRepository;
    }

    // Consultar todas las asignaciones
    public List<Asignacion> listarTodas() {
        return asignacionRepository.findAll();
    }

    // Consultar asignación por ID
    public Optional<Asignacion> buscarPorId(Integer id) {
        return asignacionRepository.findById(id);
    }

    // Buscar asignación mediante código
    public Optional<Asignacion> buscarPorCodigo(String codigo) {
        return asignacionRepository.findByCodigoTemporal(codigo);
    }

    // Consultar asignaciones de un estudiante
    public List<Asignacion> buscarPorEstudiante(String alias) {
        return asignacionRepository.findByEstudianteAlias(alias);
    }

    // Crear una asignación
    public Asignacion crearAsignacion(Asignacion asignacion) {

        asignacion.setId(null);

        // Generar un código que no esté registrado
        String codigo;

        do {
            codigo = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10)
                    .toUpperCase(Locale.ROOT);

        } while (
                asignacionRepository
                        .findByCodigoTemporal(codigo)
                        .isPresent()
        );

        asignacion.setCodigoTemporal(codigo);
        asignacion.setEstado("Pendiente");
        asignacion.setFechaAsignacion(
                System.currentTimeMillis()
        );

        return asignacionRepository.save(asignacion);
    }
}