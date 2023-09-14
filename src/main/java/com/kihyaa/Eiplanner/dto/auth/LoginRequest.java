package com.kihyaa.Eiplanner.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Schema(example = "test@gmail.com")
        String email,
        @Schema(example = "testpassword")
        String password) {

        public static LoginRequest of(String email, String password) {
                return LoginRequest.builder()
                        .password(password)
                        .email(email)
                        .build();
        }
}
