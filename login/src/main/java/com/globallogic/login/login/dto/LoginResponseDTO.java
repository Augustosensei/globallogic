package com.globallogic.login.login.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponseDTO {
	private String token;
	private String email;
	private LocalDateTime lastLogin;
}