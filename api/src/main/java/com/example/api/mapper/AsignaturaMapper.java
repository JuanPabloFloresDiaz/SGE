package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateAsignaturaRequest;
import com.example.api.dto.request.UpdateAsignaturaRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.model.Asignatura;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AsignaturaMapper {
    AsignaturaResponse toResponse(Asignatura asignatura);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Asignatura toEntity(CreateAsignaturaRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateAsignaturaRequest request, @MappingTarget Asignatura entity);
}
