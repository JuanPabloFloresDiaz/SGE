package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateEstudianteRequest;
import com.example.api.dto.request.UpdateEstudianteRequest;
import com.example.api.dto.response.EstudianteResponse;
import com.example.api.model.Estudiante;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { UsuarioMapper.class })
public interface EstudianteMapper {
    EstudianteResponse toResponse(Estudiante estudiante);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "genero", defaultValue = "O")
    @Mapping(target = "ingreso", expression = "java(request.ingreso() != null ? request.ingreso() : java.time.LocalDate.now())")
    @Mapping(target = "activo", defaultValue = "true")
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "asistencias", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    Estudiante toEntity(CreateEstudianteRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "asistencias", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    void updateEntityFromDto(UpdateEstudianteRequest request, @MappingTarget Estudiante entity);
}
