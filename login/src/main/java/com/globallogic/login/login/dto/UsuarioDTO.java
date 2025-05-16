package com.globallogic.login.login.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UsuarioDTO {
    

    private String name;
    private String email;
    private String password;
}
