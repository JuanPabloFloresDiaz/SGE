package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.EstudianteResponse;
import com.example.api.model.Estudiante;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { UsuarioMapper.class })
public interface EstudianteMapper {
    EstudianteResponse toResponse(Estudiante estudiante);
}
