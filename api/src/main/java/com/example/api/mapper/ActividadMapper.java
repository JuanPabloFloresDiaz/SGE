package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.ActividadResponse;
import com.example.api.model.Actividad;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { AsignaturaMapper.class,
        ProfesorMapper.class })
public interface ActividadMapper {

    ActividadResponse toResponse(Actividad actividad);
}
