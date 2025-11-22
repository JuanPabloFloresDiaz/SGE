package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreatePeriodoRequest;
import com.example.api.dto.request.UpdatePeriodoRequest;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.model.Periodo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PeriodoMapper {
    PeriodoResponse toResponse(Periodo periodo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "activo", defaultValue = "true")
    Periodo toEntity(CreatePeriodoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdatePeriodoRequest request, @MappingTarget Periodo entity);
}
