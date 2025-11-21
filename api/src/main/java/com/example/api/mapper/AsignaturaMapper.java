package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.model.Asignatura;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AsignaturaMapper {
    AsignaturaResponse toResponse(Asignatura asignatura);
}
