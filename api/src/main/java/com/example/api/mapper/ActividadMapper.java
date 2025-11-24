package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateActividadRequest;
import com.example.api.dto.request.UpdateActividadRequest;
import com.example.api.dto.response.ActividadResponse;
import com.example.api.model.Actividad;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { AsignaturaMapper.class,
        ProfesorMapper.class, TiposPonderacionCursoMapper.class })
public interface ActividadMapper {

    ActividadResponse toResponse(Actividad actividad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "tipoPonderacion", ignore = true)
    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "activo", defaultValue = "true")
    Actividad toEntity(CreateActividadRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "tipoPonderacion", ignore = true)
    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateActividadRequest request, @MappingTarget Actividad entity);
}
