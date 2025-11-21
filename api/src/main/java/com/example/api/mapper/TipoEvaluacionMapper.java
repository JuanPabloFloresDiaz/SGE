package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.model.TipoEvaluacion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TipoEvaluacionMapper {

    TipoEvaluacionResponse toResponse(TipoEvaluacion tipoEvaluacion);
}
