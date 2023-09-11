package com.kihyaa.Eiplanner.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    int code;
    LocalDateTime time_stamp;
    String message;
}
