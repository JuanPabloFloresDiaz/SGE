package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateProfesorRequest;
import com.example.api.dto.request.UpdateProfesorRequest;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.model.Profesor;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { UsuarioMapper.class })
public interface ProfesorMapper {
    ProfesorResponse toResponse(Profesor profesor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "contrato", defaultValue = "eventual")
    @Mapping(target = "activo", defaultValue = "true")
    Profesor toEntity(CreateProfesorRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateProfesorRequest request, @MappingTarget Profesor entity);
}
