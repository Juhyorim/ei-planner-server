package com.kihyaa.Eiplanner.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    @JsonProperty("time_stamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timeStamp;
    String message;


    public static ResponseEntity<ApiResponse> createResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message(message)
                        .build(),
                status
        );
    }
}
