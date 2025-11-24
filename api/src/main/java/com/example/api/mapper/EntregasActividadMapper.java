package com.example.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateEntregasActividadRequest;
import com.example.api.dto.request.UpdateEntregasActividadRequest;
import com.example.api.dto.response.EntregasActividadResponse;
import com.example.api.model.EntregasActividad;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EntregasActividadMapper {

    @Mapping(target = "actividadId", source = "actividad.id")
    @Mapping(target = "estudianteId", source = "estudiante.id")
    EntregasActividadResponse toResponse(EntregasActividad entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "fechaEntrega", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    EntregasActividad toEntity(CreateEntregasActividadRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "fechaEntrega", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateEntregasActividadRequest request, @MappingTarget EntregasActividad entity);
}
