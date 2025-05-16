package com.globallogic.login.login.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.globallogic.login.login.exception.ClienteException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
	public LoginResponseDTO login(final LoginRequestDTO request) {

		/* 1️⃣  JWT provisional para que el interceptor Feign lo envíe. */
		String provisionalJwt = jwtUtils.generateToken(request.getEmail());
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(request.getEmail(), provisionalJwt, new ArrayList<>()));

		/* 2️⃣  Llamo al microservicio de usuarios para obtener los datos. */
		UsuarioDTO usuario;
		try {
			usuario = usuarioClient.getByEmail(request.getEmail());
		} catch (FeignException e) {
			throw new ClienteException("No se pudo obtener el usuario", HttpStatus.BAD_GATEWAY);
		}

		/* 3️⃣  Valido la contraseña usando BCrypt. */
		if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
			throw new ClienteException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
		}

		/* 4️⃣  Genero JWT definitivo y actualizo el contexto. */
		String finalJwt = jwtUtils.generateToken(usuario.getEmail());
		SecurityContextHolder.clearContext();
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(usuario.getEmail(), finalJwt, new ArrayList<>()));

		/* 5️⃣  Construyo la respuesta completa. */
		return LoginResponseDTO.builder()
				.token(finalJwt)
				.email(usuario.getEmail())
				.lastLogin(LocalDateTime.now())
				.build();
	}
}
