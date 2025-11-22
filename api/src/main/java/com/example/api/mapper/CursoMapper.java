package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateCursoRequest;
import com.example.api.dto.request.UpdateCursoRequest;
import com.example.api.dto.response.CursoResponse;
import com.example.api.model.Curso;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { AsignaturaMapper.class, ProfesorMapper.class,
        PeriodoMapper.class })
public interface CursoMapper {
    CursoResponse toResponse(Curso curso);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asignatura", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "periodo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Curso toEntity(CreateCursoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "asignatura", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "periodo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateCursoRequest request, @MappingTarget Curso entity);
}
