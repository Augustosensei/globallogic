package com.globallogic.login.login.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
public class UsuarioDTO {

    private UUID id;
    private LocalDateTime created;
    private LocalDateTime      lastLogin;
    private String             token;
    private Boolean            isActive;
    private String             name;
    private String             email;
    private String             password;
    private List<TelefonoDTO> phones;
}
