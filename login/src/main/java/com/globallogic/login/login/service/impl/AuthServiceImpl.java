package com.globallogic.login.login.service.impl;

import com.globallogic.login.login.client.UsuarioClient;
import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.dto.UsuarioDTO;
import com.globallogic.login.login.exception.ClienteException;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.IAuthService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UsuarioClient usuarioClient;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(final LoginRequestDTO request) {


        String provisionalJwt = jwtUtils.generateToken(request.getEmail());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(request.getEmail(), provisionalJwt, new ArrayList<>()));

        UsuarioDTO usuario;
        try {
            usuario = usuarioClient.getByEmail(request.getEmail());
        } catch (FeignException e) {
            throw new ClienteException("No se pudo obtener el usuario", HttpStatus.BAD_GATEWAY);
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ClienteException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }


        String finalJwt = jwtUtils.generateToken(usuario.getEmail());
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getEmail(), finalJwt, new ArrayList<>()));


        return LoginResponseDTO.builder()
                .token(finalJwt)
                .email(usuario.getEmail())
                .lastLogin(LocalDateTime.now())
                .build();
    }
}
