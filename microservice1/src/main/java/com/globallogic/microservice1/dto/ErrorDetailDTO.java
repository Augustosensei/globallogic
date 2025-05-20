package com.globallogic.microservice1.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ErrorDetailDTO {

    private LocalDateTime timestamp;
    private int codigo;
    private String detail;
}
