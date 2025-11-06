package com.example.api.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.LoginRequest;
import com.example.api.dto.response.AuthResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Usuario;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio de autenticación.
 * Gestiona el login de usuarios y generación de tokens JWT.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Constructor con inyección de dependencias.
     */
    public AuthService(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param request Datos de login (username y password)
     * @return Respuesta de autenticación con token y datos del usuario
     * @throws ResourceNotFoundException Si el usuario no existe
     * @throws BadCredentialsException Si la contraseña es incorrecta
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario por username
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con username: " + request.username()));

        // Verificar si el usuario está activo
        if (!usuario.getActivo()) {
            throw new BadCredentialsException("Usuario inactivo");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtService.generateToken(usuario);

        // Convertir a DTO
        UsuarioResponse usuarioResponse = toUsuarioResponse(usuario);

        return new AuthResponse(token, usuarioResponse);
    }

    /**
     * Convierte una entidad Usuario a UsuarioResponse.
     */
    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        RolResponse rolResponse = new RolResponse(
                usuario.getRol().getId(),
                usuario.getRol().getNombre(),
                usuario.getRol().getDescripcion(),
                usuario.getRol().getCreatedAt(),
                usuario.getRol().getUpdatedAt(),
                usuario.getRol().getDeletedAt()
        );

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getActivo(),
                rolResponse,
                usuario.getCreatedAt(),
                usuario.getUpdatedAt(),
                usuario.getDeletedAt()
        );
    }
}
