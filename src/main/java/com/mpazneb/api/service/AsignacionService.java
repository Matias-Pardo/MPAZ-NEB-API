package com.mpazneb.api.service;

import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.repository.AsignacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Locale;
import java.util.UUID;
import com.mpazneb.api.repository.ActividadRepository;
@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final ActividadRepository actividadRepository;

    public AsignacionService(
            AsignacionRepository asignacionRepository,
            ActividadRepository actividadRepository
    ) {
        this.asignacionRepository = asignacionRepository;
        this.actividadRepository = actividadRepository;
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
        // Verificar que la actividad exista
        var actividad = actividadRepository.findById(asignacion.getActividadId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La actividad no existe"
                ));

// Verificar que la actividad esté activa
        if (!Boolean.TRUE.equals(actividad.getActiva())) {
            throw new IllegalArgumentException(
                    "No se puede asignar una actividad inactiva"
            );
        }
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