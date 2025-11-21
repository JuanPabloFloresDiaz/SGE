package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.ReporteResponse;
import com.example.api.model.Reporte;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { EstudianteMapper.class, CursoMapper.class,
        UsuarioMapper.class })
public interface ReporteMapper {

    ReporteResponse toResponse(Reporte reporte);
}
