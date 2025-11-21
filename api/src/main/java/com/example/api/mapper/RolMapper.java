package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.RolResponse;
import com.example.api.model.Rol;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RolMapper {
    RolResponse toResponse(Rol rol);
}
