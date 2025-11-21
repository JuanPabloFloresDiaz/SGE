package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.InscripcionResponse;
import com.example.api.model.Inscripcion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class, EstudianteMapper.class })
public interface InscripcionMapper {

    InscripcionResponse toResponse(Inscripcion inscripcion);
}
