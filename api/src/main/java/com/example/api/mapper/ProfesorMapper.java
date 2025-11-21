package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.ProfesorResponse;
import com.example.api.model.Profesor;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { UsuarioMapper.class })
public interface ProfesorMapper {
    ProfesorResponse toResponse(Profesor profesor);
}
