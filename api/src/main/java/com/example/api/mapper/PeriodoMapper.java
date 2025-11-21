package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.PeriodoResponse;
import com.example.api.model.Periodo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PeriodoMapper {
    PeriodoResponse toResponse(Periodo periodo);
}
