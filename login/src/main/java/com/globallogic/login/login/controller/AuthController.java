

package com.globallogic.login.login.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.globallogic.login.login.dto.LoginRequestDTO;
import com.globallogic.login.login.dto.LoginResponseDTO;
import com.globallogic.login.login.service.IAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final IAuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
		return ResponseEntity.ok(authService.login(request));
	}

}
