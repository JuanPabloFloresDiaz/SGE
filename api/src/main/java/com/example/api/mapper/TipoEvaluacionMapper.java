package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateTipoEvaluacionRequest;
import com.example.api.dto.request.UpdateTipoEvaluacionRequest;
import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.model.TipoEvaluacion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TipoEvaluacionMapper {

    TipoEvaluacionResponse toResponse(TipoEvaluacion tipoEvaluacion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    TipoEvaluacion toEntity(CreateTipoEvaluacionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateTipoEvaluacionRequest request, @MappingTarget TipoEvaluacion entity);
}
