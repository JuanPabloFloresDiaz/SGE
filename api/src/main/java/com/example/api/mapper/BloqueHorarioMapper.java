package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.BloqueHorarioResponse;
import com.example.api.model.BloqueHorario;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BloqueHorarioMapper {
    BloqueHorarioResponse toResponse(BloqueHorario bloqueHorario);
}
