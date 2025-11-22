package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.api.dto.request.CreateTemaRequest;
import com.example.api.dto.request.UpdateTemaRequest;
import com.example.api.dto.response.TemaResponse;
import com.example.api.model.Tema;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { UnidadMapper.class })
public interface TemaMapper {
    TemaResponse toResponse(Tema tema);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Tema toEntity(CreateTemaRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(UpdateTemaRequest request, @MappingTarget Tema entity);
}
