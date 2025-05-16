package com.globallogic.login.login.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
	private String token;
	private String email;
	private LocalDateTime lastLogin;


}