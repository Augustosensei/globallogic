package com.globallogic.login.login.exception;



import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ClienteException extends RuntimeException {
    private final HttpStatus status;

    public ClienteException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
