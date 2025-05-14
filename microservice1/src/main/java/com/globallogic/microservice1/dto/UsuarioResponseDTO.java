package com.globallogic.microservice1.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime created;
    private LocalDateTime lastLogin;
    private Boolean isActive;
    private List<TelefonoDTO> telefonos;
}
