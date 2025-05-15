package com.globallogic.login.login.service.impl;

import java.time.LocalDateTime;

import com.globallogic.login.login.exception.ClienteException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.globallogic.login.login.client.UsuarioClient;
import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.dto.UsuarioDTO;
import com.globallogic.login.login.security.JwtUtils;
import com.globallogic.login.login.service.IAuthService;

import lombok.RequiredArgsConstructor;
import feign.FeignException;
import org.springframework.http.HttpStatus;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    


private final UsuarioClient usuarioClient;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;

	@Override
	public LoginResponseDTO login(LoginRequestDTO request) {
		UsuarioDTO usuario;
		try {
			usuario = usuarioClient.getByEmail(request.getEmail());
		} catch (FeignException.NotFound ex) {
			// Cuando el usuario no existe en el microservicio de usuarios
			throw new ClienteException("Usuario no encontrado", HttpStatus.NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
			throw new ClienteException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
		}

		String token = jwtUtils.generateToken(usuario.getEmail());
		return LoginResponseDTO.builder()
				.token(token)
				.email(usuario.getEmail())
				.lastLogin(LocalDateTime.now())
				.build();
	}

}
