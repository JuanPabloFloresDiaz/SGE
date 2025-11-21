package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.HorarioCursoResponse;
import com.example.api.model.HorarioCurso;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class,
        BloqueHorarioMapper.class })
public interface HorarioCursoMapper {
    HorarioCursoResponse toResponse(HorarioCurso horarioCurso);
}
