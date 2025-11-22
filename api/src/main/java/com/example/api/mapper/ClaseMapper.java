package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateClaseRequest;
import com.example.api.dto.request.UpdateClaseRequest;
import com.example.api.dto.response.ClaseResponse;
import com.example.api.model.Clase;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class, UnidadMapper.class,
        TemaMapper.class })
public interface ClaseMapper {
    ClaseResponse toResponse(Clase clase);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "tema", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Clase toEntity(CreateClaseRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "tema", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateClaseRequest request, @MappingTarget Clase entity);
}
