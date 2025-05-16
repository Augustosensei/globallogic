package com.globallogic.microservice1.dto;

import javax.validation.constraints.NotNull;


import lombok.Data;


@Data
public class TelefonoDTO {
    
    @NotNull(message = "El número es obligatorio")
    private Long number;

    @NotNull(message = "El código de ciudad es obligatorio")
    private Integer citycode;

    @NotNull(message = "El código de país es obligatorio")
    private String contrycode;
}
