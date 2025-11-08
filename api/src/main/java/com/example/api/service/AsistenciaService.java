package com.example.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateAsistenciaRequest;
import com.example.api.dto.request.UpdateAsistenciaRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.AsistenciaResponse;
import com.example.api.dto.response.ClaseResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.EstudianteResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.TemaResponse;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Asistencia;
import com.example.api.model.Asistencia.EstadoAsistencia;
import com.example.api.model.Clase;
import com.example.api.model.Estudiante;
import com.example.api.model.Usuario;
import com.example.api.repository.AsistenciaRepository;
import com.example.api.repository.ClaseRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de asistencias.
 */
@Service
@Transactional
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final ClaseRepository claseRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                            ClaseRepository claseRepository,
                            EstudianteRepository estudianteRepository,
                            UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.claseRepository = claseRepository;
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Convierte una entidad Asistencia a AsistenciaResponse.
     */
    private AsistenciaResponse toResponse(Asistencia asistencia) {
        // Construir ClaseResponse
        Clase clase = asistencia.getClase();
        
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
                clase.getCurso().getAsignatura().getId(),
                clase.getCurso().getAsignatura().getCodigo(),
                clase.getCurso().getAsignatura().getNombre(),
                clase.getCurso().getAsignatura().getDescripcion(),
                clase.getCurso().getAsignatura().getCreatedAt(),
                clase.getCurso().getAsignatura().getUpdatedAt(),
                clase.getCurso().getAsignatura().getDeletedAt()
        );

        RolResponse rolResponse = new RolResponse(
                clase.getCurso().getProfesor().getUsuario().getRol().getId(),
                clase.getCurso().getProfesor().getUsuario().getRol().getNombre(),
                clase.getCurso().getProfesor().getUsuario().getRol().getDescripcion(),
                clase.getCurso().getProfesor().getUsuario().getRol().getCreatedAt(),
                clase.getCurso().getProfesor().getUsuario().getRol().getUpdatedAt(),
                clase.getCurso().getProfesor().getUsuario().getRol().getDeletedAt()
        );

        UsuarioResponse usuarioProfesorResponse = new UsuarioResponse(
                clase.getCurso().getProfesor().getUsuario().getId(),
                clase.getCurso().getProfesor().getUsuario().getUsername(),
                clase.getCurso().getProfesor().getUsuario().getNombre(),
                clase.getCurso().getProfesor().getUsuario().getEmail(),
                clase.getCurso().getProfesor().getUsuario().getTelefono(),
                clase.getCurso().getProfesor().getUsuario().getActivo(),
                rolResponse,
                clase.getCurso().getProfesor().getUsuario().getCreatedAt(),
                clase.getCurso().getProfesor().getUsuario().getUpdatedAt(),
                clase.getCurso().getProfesor().getUsuario().getDeletedAt()
        );

        ProfesorResponse profesorResponse = new ProfesorResponse(
                clase.getCurso().getProfesor().getId(),
                usuarioProfesorResponse,
                clase.getCurso().getProfesor().getEspecialidad(),
                clase.getCurso().getProfesor().getContrato(),
                clase.getCurso().getProfesor().getActivo(),
                clase.getCurso().getProfesor().getCreatedAt(),
                clase.getCurso().getProfesor().getUpdatedAt(),
                clase.getCurso().getProfesor().getDeletedAt()
        );

        PeriodoResponse periodoResponse = new PeriodoResponse(
                clase.getCurso().getPeriodo().getId(),
                clase.getCurso().getPeriodo().getNombre(),
                clase.getCurso().getPeriodo().getFechaInicio(),
                clase.getCurso().getPeriodo().getFechaFin(),
                clase.getCurso().getPeriodo().getActivo(),
                clase.getCurso().getPeriodo().getCreatedAt(),
                clase.getCurso().getPeriodo().getUpdatedAt(),
                clase.getCurso().getPeriodo().getDeletedAt()
        );

        CursoResponse cursoResponse = new CursoResponse(
                clase.getCurso().getId(),
                asignaturaResponse,
                profesorResponse,
                periodoResponse,
                clase.getCurso().getNombreGrupo(),
                clase.getCurso().getAulaDefault(),
                clase.getCurso().getCupo(),
                clase.getCurso().getCreatedAt(),
                clase.getCurso().getUpdatedAt(),
                clase.getCurso().getDeletedAt()
        );

        UnidadResponse unidadResponse = null;
        if (clase.getUnidad() != null) {
            unidadResponse = new UnidadResponse(
                    clase.getUnidad().getId(),
                    cursoResponse,
                    clase.getUnidad().getTitulo(),
                    clase.getUnidad().getDescripcion(),
                    clase.getUnidad().getNumero(),
                    clase.getUnidad().getCreatedAt(),
                    clase.getUnidad().getUpdatedAt(),
                    clase.getUnidad().getDeletedAt()
            );
        }

        TemaResponse temaResponse = null;
        if (clase.getTema() != null) {
            AsignaturaResponse asignaturaDelTema = new AsignaturaResponse(
                    clase.getTema().getUnidad().getCurso().getAsignatura().getId(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getCodigo(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getNombre(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getDescripcion(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getDeletedAt()
            );

            RolResponse rolDelTema = new RolResponse(
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getId(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getNombre(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getDescripcion(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getDeletedAt()
            );

            UsuarioResponse usuarioDelTema = new UsuarioResponse(
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getId(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getUsername(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getNombre(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getEmail(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getTelefono(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getActivo(),
                    rolDelTema,
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getDeletedAt()
            );

            ProfesorResponse profesorDelTema = new ProfesorResponse(
                    clase.getTema().getUnidad().getCurso().getProfesor().getId(),
                    usuarioDelTema,
                    clase.getTema().getUnidad().getCurso().getProfesor().getEspecialidad(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getContrato(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getActivo(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getDeletedAt()
            );

            PeriodoResponse periodoDelTema = new PeriodoResponse(
                    clase.getTema().getUnidad().getCurso().getPeriodo().getId(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getNombre(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getFechaInicio(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getFechaFin(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getActivo(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getDeletedAt()
            );

            CursoResponse cursoDelTema = new CursoResponse(
                    clase.getTema().getUnidad().getCurso().getId(),
                    asignaturaDelTema,
                    profesorDelTema,
                    periodoDelTema,
                    clase.getTema().getUnidad().getCurso().getNombreGrupo(),
                    clase.getTema().getUnidad().getCurso().getAulaDefault(),
                    clase.getTema().getUnidad().getCurso().getCupo(),
                    clase.getTema().getUnidad().getCurso().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getDeletedAt()
            );

            UnidadResponse unidadDelTema = new UnidadResponse(
                    clase.getTema().getUnidad().getId(),
                    cursoDelTema,
                    clase.getTema().getUnidad().getTitulo(),
                    clase.getTema().getUnidad().getDescripcion(),
                    clase.getTema().getUnidad().getNumero(),
                    clase.getTema().getUnidad().getCreatedAt(),
                    clase.getTema().getUnidad().getUpdatedAt(),
                    clase.getTema().getUnidad().getDeletedAt()
            );

            temaResponse = new TemaResponse(
                    clase.getTema().getId(),
                    unidadDelTema,
                    clase.getTema().getTitulo(),
                    clase.getTema().getDescripcion(),
                    clase.getTema().getNumero(),
                    clase.getTema().getDuracionMinutos(),
                    clase.getTema().getCreatedAt(),
                    clase.getTema().getUpdatedAt(),
                    clase.getTema().getDeletedAt()
            );
        }

        ClaseResponse claseResponse = new ClaseResponse(
                clase.getId(),
                cursoResponse,
                clase.getFecha(),
                clase.getInicio(),
                clase.getFin(),
                unidadResponse,
                temaResponse,
                clase.getNotas(),
                clase.getCreatedAt(),
                clase.getUpdatedAt(),
                clase.getDeletedAt()
        );

        // Construir EstudianteResponse
        Estudiante estudiante = asistencia.getEstudiante();
        
        RolResponse rolEstudiante = new RolResponse(
                estudiante.getUsuario().getRol().getId(),
                estudiante.getUsuario().getRol().getNombre(),
                estudiante.getUsuario().getRol().getDescripcion(),
                estudiante.getUsuario().getRol().getCreatedAt(),
                estudiante.getUsuario().getRol().getUpdatedAt(),
                estudiante.getUsuario().getRol().getDeletedAt()
        );

        UsuarioResponse usuarioEstudianteResponse = new UsuarioResponse(
                estudiante.getUsuario().getId(),
                estudiante.getUsuario().getUsername(),
                estudiante.getUsuario().getNombre(),
                estudiante.getUsuario().getEmail(),
                estudiante.getUsuario().getTelefono(),
                estudiante.getUsuario().getActivo(),
                rolEstudiante,
                estudiante.getUsuario().getCreatedAt(),
                estudiante.getUsuario().getUpdatedAt(),
                estudiante.getUsuario().getDeletedAt()
        );

        EstudianteResponse estudianteResponse = new EstudianteResponse(
                estudiante.getId(),
                usuarioEstudianteResponse,
                estudiante.getCodigoEstudiante(),
                estudiante.getFechaNacimiento(),
                estudiante.getDireccion(),
                estudiante.getGenero(),
                estudiante.getIngreso(),
                estudiante.getActivo(),
                estudiante.getCreatedAt(),
                estudiante.getUpdatedAt(),
                estudiante.getDeletedAt()
        );

        // Construir UsuarioResponse para registradoPor
        UsuarioResponse registradoPorResponse = null;
        if (asistencia.getRegistradoPor() != null) {
            RolResponse rolRegistradoPor = new RolResponse(
                    asistencia.getRegistradoPor().getRol().getId(),
                    asistencia.getRegistradoPor().getRol().getNombre(),
                    asistencia.getRegistradoPor().getRol().getDescripcion(),
                    asistencia.getRegistradoPor().getRol().getCreatedAt(),
                    asistencia.getRegistradoPor().getRol().getUpdatedAt(),
                    asistencia.getRegistradoPor().getRol().getDeletedAt()
            );

            registradoPorResponse = new UsuarioResponse(
                    asistencia.getRegistradoPor().getId(),
                    asistencia.getRegistradoPor().getUsername(),
                    asistencia.getRegistradoPor().getNombre(),
                    asistencia.getRegistradoPor().getEmail(),
                    asistencia.getRegistradoPor().getTelefono(),
                    asistencia.getRegistradoPor().getActivo(),
                    rolRegistradoPor,
                    asistencia.getRegistradoPor().getCreatedAt(),
                    asistencia.getRegistradoPor().getUpdatedAt(),
                    asistencia.getRegistradoPor().getDeletedAt()
            );
        }

        return new AsistenciaResponse(
                asistencia.getId(),
                claseResponse,
                estudianteResponse,
                asistencia.getEstado(),
                asistencia.getObservacion(),
                registradoPorResponse,
                asistencia.getRegistradoAt(),
                asistencia.getCreatedAt(),
                asistencia.getUpdatedAt(),
                asistencia.getDeletedAt()
        );
    }

    /**
     * Obtiene todas las asistencias activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<AsistenciaResponse> getAllAsistencias(Pageable pageable) {
        return asistenciaRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene una asistencia por su ID.
     */
    @Transactional(readOnly = true)
    public AsistenciaResponse getAsistenciaById(String id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada con ID: " + id));
        return toResponse(asistencia);
    }

    /**
     * Busca asistencias por clase.
     */
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> getAsistenciasByClaseId(String claseId) {
        return asistenciaRepository.findByClaseId(claseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca asistencias por estudiante.
     */
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> getAsistenciasByEstudianteId(String estudianteId) {
        return asistenciaRepository.findByEstudianteId(estudianteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de asistencia de un estudiante usando HashMap.
     * Implementa el uso de Tabla Hash como estructura de datos.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstadisticasEstudiante(String estudianteId) {
        List<Asistencia> asistencias = asistenciaRepository.findByEstudianteId(estudianteId);
        
        // Usar HashMap para contar frecuencia de estados
        HashMap<EstadoAsistencia, Integer> conteoEstados = new HashMap<>();
        
        // Inicializar contadores
        for (EstadoAsistencia estado : EstadoAsistencia.values()) {
            conteoEstados.put(estado, 0);
        }
        
        // Contar cada estado
        for (Asistencia asistencia : asistencias) {
            EstadoAsistencia estado = asistencia.getEstado();
            conteoEstados.put(estado, conteoEstados.get(estado) + 1);
        }
        
        int totalClases = asistencias.size();
        double porcentajeAsistencia = totalClases > 0 
                ? (conteoEstados.get(EstadoAsistencia.presente) + conteoEstados.get(EstadoAsistencia.tarde)) * 100.0 / totalClases
                : 0.0;
        
        // Construir respuesta
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("estudianteId", estudianteId);
        estadisticas.put("totalClases", totalClases);
        estadisticas.put("presente", conteoEstados.get(EstadoAsistencia.presente));
        estadisticas.put("ausente", conteoEstados.get(EstadoAsistencia.ausente));
        estadisticas.put("tarde", conteoEstados.get(EstadoAsistencia.tarde));
        estadisticas.put("justificado", conteoEstados.get(EstadoAsistencia.justificado));
        estadisticas.put("porcentajeAsistencia", Math.round(porcentajeAsistencia * 100.0) / 100.0);
        
        return estadisticas;
    }

    /**
     * Obtiene todas las asistencias eliminadas.
     */
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> getAsistenciasDeleted() {
        return asistenciaRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva asistencia.
     */
    public AsistenciaResponse createAsistencia(CreateAsistenciaRequest request) {
        // Validar que la clase existe
        Clase clase = claseRepository.findById(request.claseId())
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + request.claseId()));

        // Validar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.estudianteId()));

        // Validar usuario que registra si se proporciona
        Usuario registradoPor = null;
        if (request.registradoPorId() != null && !request.registradoPorId().isBlank()) {
            registradoPor = usuarioRepository.findById(request.registradoPorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.registradoPorId()));
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setClase(clase);
        asistencia.setEstudiante(estudiante);
        asistencia.setEstado(request.estado());
        asistencia.setObservacion(request.observacion());
        asistencia.setRegistradoPor(registradoPor);
        asistencia.setRegistradoAt(request.registradoAt() != null ? request.registradoAt() : LocalDateTime.now());

        Asistencia savedAsistencia = asistenciaRepository.save(asistencia);
        return toResponse(savedAsistencia);
    }

    /**
     * Actualiza una asistencia existente.
     */
    public AsistenciaResponse updateAsistencia(String id, UpdateAsistenciaRequest request) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada con ID: " + id));

        if (request.estado() != null) {
            asistencia.setEstado(request.estado());
        }
        if (request.observacion() != null) {
            asistencia.setObservacion(request.observacion());
        }
        if (request.registradoPorId() != null) {
            if (request.registradoPorId().isBlank()) {
                asistencia.setRegistradoPor(null);
            } else {
                Usuario registradoPor = usuarioRepository.findById(request.registradoPorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.registradoPorId()));
                asistencia.setRegistradoPor(registradoPor);
            }
        }
        if (request.registradoAt() != null) {
            asistencia.setRegistradoAt(request.registradoAt());
        }

        Asistencia updatedAsistencia = asistenciaRepository.save(asistencia);
        return toResponse(updatedAsistencia);
    }

    /**
     * Elimina lógicamente una asistencia (soft delete).
     */
    public void deleteAsistencia(String id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada con ID: " + id));
        asistencia.setDeletedAt(LocalDateTime.now());
        asistenciaRepository.save(asistencia);
    }

    /**
     * Elimina permanentemente una asistencia.
     */
    public void permanentDeleteAsistencia(String id) {
        if (!asistenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asistencia no encontrada con ID: " + id);
        }
        asistenciaRepository.deleteById(id);
    }

    /**
     * Restaura una asistencia eliminada.
     */
    public AsistenciaResponse restoreAsistencia(String id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia no encontrada con ID: " + id));
        asistencia.setDeletedAt(null);
        Asistencia restoredAsistencia = asistenciaRepository.save(asistencia);
        return toResponse(restoredAsistencia);
    }
}
