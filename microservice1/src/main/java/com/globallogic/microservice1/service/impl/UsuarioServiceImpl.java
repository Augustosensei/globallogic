package com.globallogic.microservice1.service.impl;


import com.globallogic.microservice1.dto.TelefonoDTO;
import com.globallogic.microservice1.dto.UsuarioDTO;
import com.globallogic.microservice1.dto.UsuarioResponseDTO;
import com.globallogic.microservice1.exception.ClienteException;
import com.globallogic.microservice1.model.Telefono;
import com.globallogic.microservice1.model.Usuario;
import com.globallogic.microservice1.repository.IUsuarioRepository;
import com.globallogic.microservice1.service.IUsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario crearUsuario(UsuarioDTO dto) {
        if (usuarioRepo.existsByEmail(dto.getEmail())) {
            throw new ClienteException("El usuario ya existe", HttpStatus.CONFLICT);
        }

        Usuario u = new Usuario();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setCreated(LocalDateTime.now());
        u.setLastLogin(LocalDateTime.now());
        u.setIsActive(true);

        List<Telefono> telefonos = Optional.ofNullable(dto.getTelefonos())
            .orElse(Collections.emptyList())
            .stream()
            .map(this::mapTelefonoDTOtoEntity)
            .peek(t -> t.setUsuario(u))
            .collect(Collectors.toList());
        u.setTelefonos(telefonos);

   
        return usuarioRepo.save(u);
    }

    @Override
    public Usuario iniciarSesion(String email) {
        Usuario u = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        u.setLastLogin(LocalDateTime.now());
        return usuarioRepo.save(u);
    }

@Override
public List<UsuarioResponseDTO> listarTodos() {
    return usuarioRepo.findAll().stream()
        .map(this::mapToResponseDTO)
        .collect(Collectors.toList());
}



    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepo.findById(id)
            .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    private Telefono mapTelefonoDTOtoEntity(TelefonoDTO dto) {
        Telefono telefono = new Telefono();
        telefono.setNumber(dto.getNumber());
        telefono.setCitycode(dto.getCitycode());
        telefono.setContrycode(dto.getContrycode());
        return telefono;
    }


@Override
public UsuarioResponseDTO buscarPerfilPorEmail(String email) {
    Usuario usuario = usuarioRepo.findByEmail(email)
        .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    return mapToResponseDTO(usuario);
}



public Usuario getEntidadPorEmail(String email) {
    return usuarioRepo.findByEmail(email)
        .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));
}



public UsuarioDTO mapToDTO(Usuario usuario) {
    UsuarioDTO dto = new UsuarioDTO();
    dto.setName(usuario.getName());
    dto.setEmail(usuario.getEmail());
    dto.setPassword(usuario.getPassword());
    var telefonos = usuario.getTelefonos().stream().map(tel -> {
        TelefonoDTO t = new TelefonoDTO();
        t.setNumber(tel.getNumber());
        t.setCitycode(tel.getCitycode());
        t.setContrycode(tel.getContrycode());
        return t;
    }).collect(Collectors.toList());

    dto.setTelefonos(telefonos);
    return dto;
}

public UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
    return UsuarioResponseDTO.builder()
        .id(usuario.getId())
        .name(usuario.getName())
        .email(usuario.getEmail())
        .created(usuario.getCreated())
        .lastLogin(usuario.getLastLogin())
        .isActive(usuario.getIsActive())
        .telefonos(
            usuario.getTelefonos().stream().map(t -> {
                TelefonoDTO dto = new TelefonoDTO();
                dto.setNumber(t.getNumber());
                dto.setCitycode(t.getCitycode());
                dto.setContrycode(t.getContrycode());
                return dto;
            }).collect(Collectors.toList())
        )
        .build();
}

@Override
public UsuarioDTO buscarPorEmail(String email) {
    Usuario usuario = usuarioRepo.findByEmail(email)
        .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    return mapToDTO(usuario);
}




}
