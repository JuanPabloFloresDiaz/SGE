package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateUnidadRequest;
import com.example.api.dto.request.UpdateUnidadRequest;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.model.Unidad;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class })
public interface UnidadMapper {
    UnidadResponse toResponse(Unidad unidad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Unidad toEntity(CreateUnidadRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateUnidadRequest request, @MappingTarget Unidad entity);
}
