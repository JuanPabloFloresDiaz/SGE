package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.CursoResponse;
import com.example.api.model.Curso;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { AsignaturaMapper.class, ProfesorMapper.class,
        PeriodoMapper.class })
public interface CursoMapper {
    CursoResponse toResponse(Curso curso);
}
