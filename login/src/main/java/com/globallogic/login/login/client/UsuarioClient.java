package com.globallogic.login.login.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.globallogic.login.login.dto.UsuarioDTO;

@FeignClient(name = "usuario-service", url = "http://localhost:8081")
public interface UsuarioClient {

	@GetMapping("/email/{email}")
	UsuarioDTO getByEmail(@PathVariable("email") String email);
}
