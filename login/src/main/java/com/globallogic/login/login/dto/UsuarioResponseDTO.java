package com.globallogic.login.login.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
public class UsuarioResponseDTO {

    private UUID id;
    private LocalDateTime created;
    private LocalDateTime      lastLogin;
    private String             token;
    private Boolean            isActive;
    private String             name;
    private String             email;
    private String             password;
    @JsonProperty("telefonos")
    private List<TelefonoDTO> phones;
}
