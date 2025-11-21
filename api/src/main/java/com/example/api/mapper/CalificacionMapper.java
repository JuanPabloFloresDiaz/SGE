package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.CalificacionResponse;
import com.example.api.model.Calificacion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { EvaluacionMapper.class,
        EstudianteMapper.class })
public interface CalificacionMapper {

    CalificacionResponse toResponse(Calificacion calificacion);
}
