package com.globallogic.login.login.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteException.class)
    public ResponseEntity<ErrorDetailDTO> handleClienteException(ClienteException ex) {
        var error = ErrorDetailDTO.builder()
                .timestamp(LocalDateTime.now())
                .codigo(ex.getStatus().value())
                .detail(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailDTO> handleGeneralException(Exception ex) {
        var error = ErrorDetailDTO.builder()
                .timestamp(LocalDateTime.now())
                .codigo(500)
                .detail("Error inesperado: " + ex.getMessage())
                .build();

        return ResponseEntity.internalServerError().body(error);
    }


}
