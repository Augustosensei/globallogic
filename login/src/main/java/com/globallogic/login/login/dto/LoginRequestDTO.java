
package com.globallogic.login.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDTO {
	private String email;
	private String password;


}