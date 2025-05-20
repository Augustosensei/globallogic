package com.globallogic.login.login.client;

import com.globallogic.login.login.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-service", url = "http://localhost:8081")
public interface UsuarioClient {

    @GetMapping("/email/{email}")
    UsuarioDTO getByEmail(@PathVariable("email") String email);
}
