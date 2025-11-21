package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.AsistenciaResponse;
import com.example.api.model.Asistencia;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { ClaseMapper.class, EstudianteMapper.class,
        UsuarioMapper.class })
public interface AsistenciaMapper {

    AsistenciaResponse toResponse(Asistencia asistencia);
}
