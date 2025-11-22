package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateHorarioCursoRequest;
import com.example.api.dto.request.UpdateHorarioCursoRequest;
import com.example.api.dto.response.HorarioCursoResponse;
import com.example.api.model.HorarioCurso;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class,
        BloqueHorarioMapper.class })
public interface HorarioCursoMapper {
    HorarioCursoResponse toResponse(HorarioCurso horarioCurso);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "bloqueHorario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    HorarioCurso toEntity(CreateHorarioCursoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "bloqueHorario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateHorarioCursoRequest request, @MappingTarget HorarioCurso entity);
}
