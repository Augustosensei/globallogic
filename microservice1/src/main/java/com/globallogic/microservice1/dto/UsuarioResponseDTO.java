package com.globallogic.microservice1.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UsuarioResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private LocalDateTime created;
    private LocalDateTime lastLogin;
    private Boolean isActive;
    private List<TelefonoDTO> telefonos;
    private String password;
}
