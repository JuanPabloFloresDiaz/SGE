package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateBloqueHorarioRequest;
import com.example.api.dto.request.UpdateBloqueHorarioRequest;
import com.example.api.dto.response.BloqueHorarioResponse;
import com.example.api.model.BloqueHorario;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BloqueHorarioMapper {
    BloqueHorarioResponse toResponse(BloqueHorario bloqueHorario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    BloqueHorario toEntity(CreateBloqueHorarioRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateBloqueHorarioRequest request, @MappingTarget BloqueHorario entity);
}
