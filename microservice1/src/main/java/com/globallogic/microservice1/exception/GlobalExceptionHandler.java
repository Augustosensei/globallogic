package com.globallogic.microservice1.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.globallogic.microservice1.dto.ErrorDetailDTO;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


       @ExceptionHandler(ClienteException.class)
    public ResponseEntity<ErrorDetailDTO> handleClienteException(ClienteException ex) {
        ErrorDetailDTO error = ErrorDetailDTO.builder()
            .timestamp(LocalDateTime.now())
            .codigo(ex.getStatus().value())
            .detail(ex.getMessage())
            .build();
        return ResponseEntity
            .status(ex.getStatus())
            .body(error);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailDTO> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .findFirst()
            .orElse(ex.getMessage());

        ErrorDetailDTO error = ErrorDetailDTO.builder()
            .timestamp(LocalDateTime.now())
            .codigo(org.springframework.http.HttpStatus.BAD_REQUEST.value())
            .detail(message)
            .build();
        return ResponseEntity
            .badRequest()
            .body(error);
    }
}