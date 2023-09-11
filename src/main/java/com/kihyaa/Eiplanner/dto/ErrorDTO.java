package com.kihyaa.Eiplanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorDTO(String error, String message, @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp) {
    public ErrorDTO(String error, String message) {
        this(error, message, LocalDateTime.now());
    }
}