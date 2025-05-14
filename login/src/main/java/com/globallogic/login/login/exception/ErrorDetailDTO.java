package com.globallogic.login.login.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDetailDTO {
    private LocalDateTime timestamp;
    private int codigo;
    private String detail;
}