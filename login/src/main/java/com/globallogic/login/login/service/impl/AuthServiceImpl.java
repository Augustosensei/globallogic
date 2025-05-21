package com.globallogic.login.login.service.impl;

import com.globallogic.login.login.client.UsuarioClient;
import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.dto.TelefonoDTO;
import com.globallogic.login.login.dto.UsuarioResponseDTO;
import com.globallogic.login.login.exception.ClienteException;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.IAuthService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UsuarioClient usuarioClient;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(final LoginRequestDTO request) {


        String provisionalJwt = jwtUtils.generateToken(request.getEmail());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(request.getEmail(), provisionalJwt, new ArrayList<>()));

        UsuarioResponseDTO usuario;
        try {
            usuario = usuarioClient.getByEmail(request.getEmail());
        } catch (FeignException e) {
            throw new ClienteException("No se pudo obtener el usuario", HttpStatus.BAD_GATEWAY);
        }
        log.debug("rawPassword='{}', hashedPassword='{}'", request.getPassword(), usuario.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ClienteException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }


        String finalJwt = jwtUtils.generateToken(usuario.getEmail());
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getEmail(), finalJwt, new ArrayList<>()));


        return LoginResponseDTO.builder()
                .id(usuario.getId())
                .created(usuario.getCreated())
                .lastLogin(usuario.getLastLogin())
                .token(finalJwt)
                .isActive(usuario.getIsActive())
                .name(usuario.getName())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .phones(usuario.getPhones().stream()
                        .map(t -> TelefonoDTO.builder()
                                .number(t.getNumber())
                                .citycode(t.getCitycode())
                                .contrycode(t.getContrycode())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();

    }
}
