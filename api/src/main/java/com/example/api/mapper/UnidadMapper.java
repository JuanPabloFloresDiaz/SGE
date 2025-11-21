package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.UnidadResponse;
import com.example.api.model.Unidad;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { CursoMapper.class })
public interface UnidadMapper {
    UnidadResponse toResponse(Unidad unidad);
}
