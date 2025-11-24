package com.example.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateTiposPonderacionCursoRequest;
import com.example.api.dto.request.UpdateTiposPonderacionCursoRequest;
import com.example.api.dto.response.TiposPonderacionCursoResponse;
import com.example.api.model.TiposPonderacionCurso;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TiposPonderacionCursoMapper {

    @Mapping(target = "cursoId", source = "curso.id")
    TiposPonderacionCursoResponse toResponse(TiposPonderacionCurso entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    TiposPonderacionCurso toEntity(CreateTiposPonderacionCursoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateTiposPonderacionCursoRequest request, @MappingTarget TiposPonderacionCurso entity);
}
