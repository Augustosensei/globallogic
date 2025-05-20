package com.globallogic.login.login.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ClienteException.class)
    public ResponseEntity<ErrorDetailDTO> handleClienteException(ClienteException ex) {
        var error = buildError(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailDTO> handleBeanValidation(MethodArgumentNotValidException ex) {

        // Concatenamos todos los mensajes de error de los campos
        String detalle = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        var error = buildError(HttpStatus.BAD_REQUEST.value(), detalle);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetailDTO> handleParamValidation(ConstraintViolationException ex) {

        String detalle = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining(" | "));

        var error = buildError(HttpStatus.BAD_REQUEST.value(), detalle);
        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailDTO> handleGeneralException(Exception ex) {
        var error = buildError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error inesperado: " + ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    private ErrorDetailDTO buildError(int codigo, String detalle) {
        return ErrorDetailDTO.builder()
                .timestamp(LocalDateTime.now())
                .codigo(codigo)
                .detail(detalle)
                .build();
    }
}
