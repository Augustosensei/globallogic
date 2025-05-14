package com.globallogic.login.login.service.impl;

import java.time.LocalDateTime;

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


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    


private final UsuarioClient usuarioClient;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;

	public LoginResponseDTO login(LoginRequestDTO request) {
		UsuarioDTO usuario = usuarioClient.getByEmail(request.getEmail());

		if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
			throw new RuntimeException("Credenciales inválidas");
		}

		String token = jwtUtils.generateToken(usuario.getEmail());

		return LoginResponseDTO.builder()
			.token(token)
			.email(usuario.getEmail())
			.lastLogin(LocalDateTime.now())
			.build();
	}
}
