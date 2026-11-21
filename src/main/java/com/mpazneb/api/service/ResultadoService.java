package com.mpazneb.api.service;

import com.mpazneb.api.model.Asignacion;
import com.mpazneb.api.model.Pregunta;
import com.mpazneb.api.model.Respuesta;
import com.mpazneb.api.model.Resultado;
import com.mpazneb.api.repository.AsignacionRepository;
import com.mpazneb.api.repository.PreguntaRepository;
import com.mpazneb.api.repository.RespuestaRepository;
import com.mpazneb.api.repository.ResultadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ResultadoService {

    private final ResultadoRepository resultadoRepository;
    private final AsignacionRepository asignacionRepository;
    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;

    public ResultadoService(
            ResultadoRepository resultadoRepository,
            AsignacionRepository asignacionRepository,
            PreguntaRepository preguntaRepository,
            RespuestaRepository respuestaRepository
    ) {
        this.resultadoRepository = resultadoRepository;
        this.asignacionRepository = asignacionRepository;
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
    }

    public List<Resultado> consultarPorAsignacion(Integer asignacionId) {
        if (!asignacionRepository.existsById(asignacionId)) {
            throw new IllegalArgumentException("La asignacion no existe");
        }
        return resultadoRepository.findAllByAsignacionId(asignacionId);
    }

    public Optional<Resultado> buscarPorAsignacion(Integer asignacionId) {
        if (!asignacionRepository.existsById(asignacionId)) {
            throw new IllegalArgumentException("La asignacion no existe");
        }
        return resultadoRepository.findByAsignacionId(asignacionId);
    }

    @Transactional
    public Resultado calcularYGuardar(Integer asignacionId) {
        Asignacion asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new IllegalArgumentException("La asignacion no existe"));

        List<Pregunta> preguntas = preguntaRepository.findByActividadId(asignacion.getActividadId());
        List<Respuesta> respuestas = respuestaRepository
                .findByAsignacionIdOrderByFechaRespuestaAscIdAsc(asignacionId);

        int aciertos = 0;
        for (Pregunta pregunta : preguntas) {
            boolean acertada = respuestas.stream().anyMatch(respuesta ->
                    respuesta.getActividadId().equals(asignacion.getActividadId())
                            && respuesta.getPreguntaId().equals(pregunta.getId())
                            && Boolean.TRUE.equals(respuesta.getEsCorrecta())
            );
            if (acertada) {
                aciertos++;
            }
        }

        int intentos = (int) respuestas.stream()
                .filter(respuesta -> respuesta.getActividadId().equals(asignacion.getActividadId()))
                .count();

        Resultado resultado = resultadoRepository.findByAsignacionId(asignacionId)
                .orElseGet(Resultado::new);
        resultado.setAsignacionId(asignacionId);
        resultado.setAciertos(aciertos);
        resultado.setTotalPreguntas(preguntas.size());
        resultado.setIntentos(intentos);
        if (resultado.getObservacion() == null) {
            resultado.setObservacion("");
        }
        if (resultado.getFechaRegistro() == null) {
            resultado.setFechaRegistro(System.currentTimeMillis());
        }

        return resultadoRepository.save(resultado);
    }
}
