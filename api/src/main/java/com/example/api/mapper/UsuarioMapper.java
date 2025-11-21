package com.example.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.example.api.dto.response.UsuarioResponse;
import com.example.api.model.Usuario;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { RolMapper.class })
public interface UsuarioMapper {
    UsuarioResponse toResponse(Usuario usuario);
}
