package com.globallogic.microservice1.service.impl;


import com.globallogic.microservice1.dto.TelefonoDTO;
import com.globallogic.microservice1.dto.UsuarioDTO;
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
    public List<Usuario> listarTodos() {
        return usuarioRepo.findAll();
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
public Usuario buscarPorEmail(String email) {
    return usuarioRepo.findByEmail(email)
        .orElseThrow(() -> new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND));
}

}
