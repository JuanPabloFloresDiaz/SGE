package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.EvaluacionResponse;
import com.example.api.model.Evaluacion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class,
        TipoEvaluacionMapper.class })
public interface EvaluacionMapper {

    EvaluacionResponse toResponse(Evaluacion evaluacion);
}
