package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.TemaResponse;
import com.example.api.model.Tema;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { UnidadMapper.class })
public interface TemaMapper {
    TemaResponse toResponse(Tema tema);
}
