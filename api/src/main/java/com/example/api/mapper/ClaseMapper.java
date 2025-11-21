package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.ClaseResponse;
import com.example.api.model.Clase;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class, UnidadMapper.class,
        TemaMapper.class })
public interface ClaseMapper {
    ClaseResponse toResponse(Clase clase);
}
