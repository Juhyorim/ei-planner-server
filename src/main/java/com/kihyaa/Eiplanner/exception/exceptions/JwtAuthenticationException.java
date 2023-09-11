package com.kihyaa.Eiplanner.exception.exceptions;

public class JwtAuthenticationException extends RuntimeException {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}